package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.tripleDoubleFromString
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.Spline
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.text.uppercase

class ParticlePath() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val path =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativePathFromString(getSection(effectShow, id).getString("Path")!!,
                        effectShow.centerLocation ?: return)
                        .map { it.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) }
                }else
                    LocationUtils.getLocationPathFromString(getSection(effectShow, id).getString("Path")!!)
            if(path.size < 2) return

            val particle = getSection(effectShow, id).getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 0
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05
            val delta = getSection(effectShow, id).getString("Delta")
                ?.let { tripleDoubleFromString(it) }
                ?: Triple(0.0, 0.0, 0.0)
            val dX = delta.first
            val dY = delta.second
            val dZ = delta.third
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0

            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

            val splineType = if (getSection(effectShow, id).get("SplineType") != null) Spline.valueOf(
                getSection(effectShow, id).getString("SplineType")!!.uppercase()
            ) else Spline.CATMULL_ROM

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The speed has to be greater than 0!")
                return
            }

            var distance = 0.0
            for(loc in 1 until path.size){
                distance += path[loc - 1].distance(path[loc])
            }

            // How long the effect is expected to last.
            val duration = distance / speed

            var c = 0.0
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                if (c >= 1) {
                    task.cancel()
                    return@runTimer
                }

                /*
                duration / distance = how many entities per block?
                if this is smaller than the frequency it has to spawn more entities in one tick

                The frequency / entities per block = how many entities per tick
                */
                if (duration / distance < frequency) {
                    val entitiesPerTick = frequency / (duration / distance)
                    for (i2 in 1..entitiesPerTick.toInt()) {
                        val progress = c + 1.0 / duration / entitiesPerTick * i2
                        if(progress > 1) continue

                        spawnParticle(
                            splineType.calculate(path, progress),
                            particle, amount, dX, dY, dZ, extra, force, players, effectShow, id
                        )
                    }
                }

                /*
                    The amount of entities per block is bigger than the frequency
                    => No need to spawn extra entities
                */
                else {
                    spawnParticle(splineType.calculate(path, c), particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                }

                c += 1.0 / duration
            }, 0L, 1L)
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
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
                    Bukkit.getOnlinePlayers().forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
                    }
                    //location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
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
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, material.createBlockData(), force)
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
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, ItemStack(material), force)
                    }
                }
            }

            else -> {
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null, force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null, force)
                    }
                }
            }
        }
    }

    override fun getIdentifier(): String {
        return "PARTICLE_PATH"
    }

    override fun getDisplayMaterial(): Material {
        return Material.COMPARATOR
    }

    override fun getDescription(): String {
        return "Spawns a path of particles."
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
            "Path",
            "world, 0, 0, 0; 1, 1, 1",
            "The path the particles follows using the format of " +
                    "\"world, x1, y1, z1; x2, y2, z2; x3, y3, z3\". You can of course repeat this process as much as you would like. Use a ; to separate different locations.",
            {it},
            { LocationUtils.getLocationPathFromString(it).isNotEmpty() })
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
            { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        )
        list.add(Parameter(
            "Frequency",
            5,
            "In Minecraft a new entity or particle spawns every tick, but when the speed is very high an empty space comes between two entities or particles. To fix that you can use the frequency parameter. The frequency is how many entities/particles there should be every block. This effect only activates when the speed is too big that the amount of entities or particles per block is lower than the frequency.",
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
            "Force",
            false,
            "Whether the particle should be forcibly rendered by the player or not.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
        )
        list.add(Parameter(
            "SplineType",
            "CATMULL_ROM",
            DefaultDescriptions.SPLINE_TYPE,
            { it.uppercase() },
            { Spline.entries.any { spline -> it.equals(spline.name, ignoreCase = true) } })
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
            {it},
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