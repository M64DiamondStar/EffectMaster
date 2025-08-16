package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.absoluteValue
import kotlin.math.max

class ParticleLine() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val fromLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("FromLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("ToLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return
            val particle = getSection(effectShow, id).getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 0
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05
            val dX = if (getSection(effectShow, id).get("dX") != null) getSection(effectShow, id).getDouble("dX") else 0.0
            val dY = if (getSection(effectShow, id).get("dY") != null) getSection(effectShow, id).getDouble("dY") else 0.0
            val dZ = if (getSection(effectShow, id).get("dZ") != null) getSection(effectShow, id).getDouble("dZ") else 0.0
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0

            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
                return
            }

            val distance = fromLocation.distance(toLocation)

            val deX: Double = (toLocation.x - fromLocation.x) / speed
            val deY: Double = (toLocation.y - fromLocation.y) / speed
            val deZ: Double = (toLocation.z - fromLocation.z) / speed

            // How long the effect is expected to last.
            val duration = max(max(deX.absoluteValue, deY.absoluteValue), deZ.absoluteValue)

            val x: Double = deX / duration / 20.0 * (speed * 20.0)
            val y: Double = deY / duration / 20.0 * (speed * 20.0)
            val z: Double = deZ / duration / 20.0 * (speed * 20.0)

            var c = 0
            val location: Location = fromLocation
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                if (c >= duration) {
                    task.cancel()
                    return@runTimer
                }

                /* duration / distance = how many entities per block?
                if this is smaller than the frequency it has to spawn more entities in one tick

                The frequency / entities per block = how many entities per tick*/
                if(duration / distance < frequency) {
                    val entitiesPerTick = frequency / (duration / distance)

                    val adjustedLocation = location.clone()
                    val adjustedX = x / entitiesPerTick
                    val adjustedY = y / entitiesPerTick
                    val adjustedZ = z / entitiesPerTick

                    repeat(entitiesPerTick.toInt()){
                        spawnParticle(adjustedLocation, particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                        adjustedLocation.add(adjustedX, adjustedY, adjustedZ)
                    }
                }

                /* The amount of entities per block is bigger than the frequency
                    => No need to spawn extra entities
                 */
                else {
                    spawnParticle(location, particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                }

                location.add(x, y, z)
                c++
            }, 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Line with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The particle you entered doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }

    }

    private fun spawnParticle(location: Location, particle: Particle, amount: Int, dX: Double, dY: Double, dZ: Double,
                              extra: Double, force: Boolean, players: List<Player>?, effectShow: EffectShow, id: Int) {
        when (particle) {
            Particle.DUST -> {
                val color = Colors.getJavaColorFromString(getSection(effectShow, id).getString("Color")!!) ?: java.awt.Color(0, 0, 0)
                val dustOptions = Particle.DustOptions(
                    Color.fromRGB(color.red, color.green, color.blue),
                    if (getSection(effectShow, id).get("Size") != null)
                        getSection(effectShow, id).getInt("Size").toFloat()
                    else
                        1F
                )
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions)
                    }
                }
            }

            Particle.BLOCK, Particle.FALLING_DUST -> {
                val material =
                    if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, material.createBlockData(), force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, material.createBlockData())
                    }
                }
            }

            Particle.ITEM -> {
                val material =
                    if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, ItemStack(material), force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, ItemStack(material))
                    }
                }
            }

            else -> {
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null, force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null)
                    }
                }
            }
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
        list.add(Parameter(
            "Particle",
            "CLOUD",
            DefaultDescriptions.PARTICLE,
            {it.uppercase()},
            { it in Particle.entries.map { it.name } })
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
            "Amount",
            50,
            "The amount of particles to spawn.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
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
            "dX",
            0.3,
            "The delta X, the value of this decides how much the area where the particle spawns will extend over the x-axis.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null })
        )
        list.add(Parameter(
            "dY",
            0.3,
            "The delta Y, the value of this decides how much the area where the particle spawns will extend over the y-axis.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null })
        )
        list.add(Parameter(
            "dZ",
            0.3,
            "The delta Z, the value of this decides how much the area where the particle spawns will extend over the z-axis.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null })
        )
        list.add(Parameter(
            "Force",
            false,
            "Whether the particle should be forcibly rendered by the player or not.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
        )
        list.add(Parameter(
            "Size",
            0.5f,
            "The size of the particle, only works for REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT.",
            {it.toFloat()},
            { it.toFloatOrNull() != null && it.toFloat() >= 0.0 })
        )
        list.add(Parameter(
            "Color",
            "0, 0, 0",
            "The color of the particle, only works for REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT. Formatted in RGB.",
            {it},
            { Colors.getJavaColorFromString(it) != null })
        )
        list.add(Parameter(
            "Block",
            "STONE",
            "The block id of the particle, only works for BLOCK_CRACK, BLOCK_DUST, FALLING_DUST and ITEM_CRACK.",
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
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