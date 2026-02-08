package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.tripleDoubleFromString
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.calculatePolygonalChain
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.ConditionalParameter
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.shows.utils.emParticle
import me.m64diamondstar.effectmaster.update.Version
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.absoluteValue
import kotlin.math.max

class ParticleLine : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val section = getSection(effectShow, id)

            val particle = section.getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val fromLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(section.getString("FromLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(section.getString("FromLocation")!!) ?: return
            val toLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(section.getString("ToLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(section.getString("ToLocation")!!) ?: return
            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05

            // COMMON PARTICLE PARAMS -- START
            val amount = if (section.get("Amount") != null) section.getInt("Amount") else 0
            val delta = section.getString("Delta")
                ?.let { tripleDoubleFromString(it) }
                ?: Triple(0.0, 0.0, 0.0)
            val particleSpeed = if (section.get("ParticleSpeed") != null) section.getDouble("ParticleSpeed") else 0.0

            // COLORED
            val color = Colors.getBukkitColorFromString(section.getString("Color") ?: "0, 0, 0") ?: Color.BLACK
            val alpha = if (section.get("Alpha") != null) section.getInt("Alpha") else 0
            val toColor = Colors.getBukkitColorFromString(section.getString("ToColor") ?: "255, 255, 255") ?: Color.WHITE
            val size = section.getDouble("Size").toFloat()

            // TRAIL & VIBRATION
            val travelLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(section.getString("TravelLocation") ?: section.getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(section.getString("TravelLocation") ?: section.getString("Location")!!) ?: return
            val trailDuration = if (section.get("TrailDuration") != null) section.getInt("TrailDuration") else 0
            val trail = Particle.Trail(travelLocation, color, trailDuration)

            // MATERIAL
            val material =
                if (section.get("Block") != null)
                    Material.valueOf(section.getString("Block")!!.uppercase())
                else Material.STONE

            // SKULK_CHARGE
            val angle = if (section.get("Angle") != null) section.getDouble("Angle") else 0.0
            val vibration = Vibration(Vibration.Destination.BlockDestination(travelLocation), trailDuration)

            val force = if (section.get("Force") != null) section.getBoolean("Force") else false
            // COMMON PARTICLE PARAMS -- END


            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The speed has to be greater than 0!")
                return
            }

            val distance = fromLocation.distance(toLocation)

            val deX: Double = (toLocation.x - fromLocation.x) / speed
            val deY: Double = (toLocation.y - fromLocation.y) / speed
            val deZ: Double = (toLocation.z - fromLocation.z) / speed

            // How long the effect is expected to last.
            val duration = max(max(deX.absoluteValue, deY.absoluteValue), deZ.absoluteValue)

            fun spawnParticle(location: Location) {
                emParticle(
                    type = particle,
                    location = location,
                    offset = delta,
                    count = amount,
                    speed = particleSpeed,
                    color = color,
                    alpha = alpha,
                    size = size,
                    toColor = toColor,
                    trail = trail,
                    blockData = material.createBlockData(),
                    itemStack = ItemStack.of(material),
                    angle = angle,
                    vibration = vibration,
                    receivers = players,
                    receiveRadius = if(force) 512 else 32
                ).spawn()
            }

            var c = 0
            effectShow.runTimer(id, { task ->
                if (c >= duration) {
                    task.cancel()
                    return@runTimer
                }

                /* duration / distance = how many entities per block?
                if this is smaller than the frequency it has to spawn more entities in one tick

                The frequency / entities per block = how many entities per tick*/
                if(duration / distance < frequency) {
                    val entitiesPerTick = frequency / (duration / distance)

                    repeat(entitiesPerTick.toInt()){
                        val i2 = it
                        val subProgress = ((c + i2.toDouble() / entitiesPerTick) / duration).coerceAtMost(1.0)
                        val interpolatedLocation = calculatePolygonalChain(listOf(fromLocation, toLocation), subProgress)
                        spawnParticle(interpolatedLocation)
                    }
                }

                /* The amount of entities per block is bigger than the frequency
                    => No need to spawn extra entities
                 */
                else {
                    val progress = c.toDouble() / duration
                    val interpolatedLocation = calculatePolygonalChain(listOf(fromLocation, toLocation), progress)
                    spawnParticle(interpolatedLocation)
                }

                c++
            }, 1L, 1L)
        } catch (ex: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play ParticleLine with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Reason: ${ex.message}")
        }

    }

    override fun getIdentifier(): String {
        return "PARTICLE_LINE"
    }

    override fun getDisplayMaterial(): Material {
        return Material.REPEATER
    }

    override fun getDescription(): String {
        return "Spawns a line of particles."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(SuggestingParameter(
            "Particle",
            "CLOUD",
            DefaultDescriptions.PARTICLE,
            {it.uppercase()},
            { Particle.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } },
            Particle.entries.map { it.name.lowercase() })
        )
        list.add(Parameter(
            "FromLocation",
            "world, 0, 0, 0",
            "The location where the particle line starts.",
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "ToLocation",
            "world, 1, 1, 1",
            "The location where the particle line ends.",
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "Speed",
            1,
            "The speed of the particle line. Measured in blocks/second.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null && it.toDouble() >= 0 })
        )
        list.add(Parameter(
            "Frequency",
            5,
            "In Minecraft a new entity or particle spawns every tick, but when the speed is very high an empty space comes between two entities or particles. To fix that you can use the frequency parameter. The frequency is how many entities/particles there should be every block. This effect only activates when the speed is too big that the amount of entities or particles per block is lower than the frequency.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Amount",
            50,
            "The amount of particles to spawn.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Delta",
            "0.0, 0.0, 0.0",
            "The value of this decides how much the area where the particle spawns will extend over the different axes.",
            { it },
            { tripleDoubleFromString(it) != null })
        )
        list.add(Parameter(
            "ParticleSpeed",
            "1.0",
            "The speed of the particle.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null })
        )
        list.add(ConditionalParameter(
            "Color",
            "0, 0, 0",
            "The color of the particle. Only works for DUST, DUST_COLOR_TRANSITION, ENTITY_EFFECT, FLASH (on 1.21.9+), TINTED_LEAVES and TRAIL. Formatted in RGB.",
            {it},
            { Colors.getJavaColorFromString(it) != null },
            { it.any { parameter -> parameter.key.name == "Particle" && (parameter.value == "DUST"
                    || parameter.value == "DUST_COLOR_TRANSITION" || parameter.value == "ENTITY_EFFECT")
                    || (parameter.value == "FLASH" && EffectMaster.getVersion() >= Version.parse("1.21.9"))
                    || parameter.value == "TINTED_LEAVES" || parameter.value == "TRAIL"} })
        )
        list.add(ConditionalParameter(
            "Alpha",
            "255",
            "The transparency of the ",
            {it.toDouble()},
            { it.toDoubleOrNull() != null },
            { it.any { parameter -> parameter.key.name == "Particle" && parameter.value == "ENTITY_EFFECT" } })
        )
        list.add(ConditionalParameter(
            "Size",
            0.5f,
            "The size of the particle. Only works for DUST and DUST_COLOR_TRANSITION.",
            {it.toFloat()},
            { it.toFloatOrNull() != null && it.toFloat() >= 0.0 },
            { it.any { parameter -> parameter.key.name == "Particle" && (parameter.value == "DUST" || parameter.value == "DUST_COLOR_TRANSITION") } })
        )
        list.add(ConditionalParameter(
            "ToColor",
            "255, 255, 255",
            "The color to transition to. Only works for DUST_COLOR_TRANSITION. Formatted in RGB.",
            {it},
            { Colors.getJavaColorFromString(it) != null },
            { it.any { parameter -> parameter.key.name == "Particle" && parameter.value == "DUST_COLOR_TRANSITION" }})
        )
        list.add(ConditionalParameter(
            "TravelLocation",
            "world, 0, 0, 0",
            "The location the particle travels to. Only works for TRAIL and VIBRATION.",
            {it},
            { LocationUtils.getLocationFromString(it) != null },
            { it.any { parameter -> parameter.key.name == "Particle" && (parameter.value == "TRAIL" || parameter.value == "VIBRATION")}})
        )
        list.add(ConditionalParameter(
            "ParticleDuration",
            "20",
            "The duration of the particle effect. Only works for VIBRATION and TRAIL.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null },
            { it.any { parameter -> parameter.key.name == "Particle" && (parameter.value == "VIBRATION" || parameter.value == "TRAIL") } })
        )
        list.add(ConditionalParameter(
            "Block",
            "STONE",
            "The block id of the particle. Only works for BLOCK, BLOCK_CRUMBLE, BLOCK_MARKER, FALLING_DUST, DUST_PILLAR and ITEM.",
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } },
            { it.any { parameter -> parameter.key.name == "Particle" && (parameter.value == "BLOCK"
                    || parameter.value == "BLOCK_CRUMBLE" || parameter.value == "BLOCK_MARKER"
                    || parameter.value == "FALLING_DUST" || parameter.value == "DUST_PILLAR"
                    || parameter.value == "ITEM")} })
        )
        list.add(ConditionalParameter(
            "Angle",
            0.0,
            "The angle the particle displays at in degrees. Only works for SKULK_CHARGE.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null && it.toDouble() >= 0 },
            { it.any { parameter -> parameter.key.name == "Particle" && (parameter.value == "SKULK_CHARGE")}})
        )
        list.add(Parameter(
            "Force",
            false,
            "Whether the particle should be forcibly rendered by the player or not.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
        )
        list.add(Parameter(
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}