package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.tripleDoubleFromString
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt
import kotlin.text.uppercase

class ParticleEmitter() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        try{
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val particle = getSection(effectShow, id).getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 0
            val delta = getSection(effectShow, id).getString("Delta")
                ?.let { tripleDoubleFromString(it) }
                ?: Triple(0.0, 0.0, 0.0)
            val dX = delta.first
            val dY = delta.second
            val dZ = delta.third
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getInt("Duration") else {
                if (getSection(effectShow, id).get("Length") != null) getSection(effectShow, id).getInt("Length") else 20
            }
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val startUp = if (getSection(effectShow, id).get("StartUp") != null) getSection(effectShow, id).getDouble("StartUp") else 0.0
            val extra = if(amount == 0) 1.0 else 0.0

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

                    var c = 0
                    EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                            if(c == duration){
                                task.cancel()
                                return@runTimer
                            }
                            if(players == null) {
                                location.world!!.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, dustOptions, force
                                )
                            }else{
                                players.forEach {
                                    it.spawnParticle(particle, location,
                                        if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                        dX, dY, dZ, extra, dustOptions)
                                }
                            }
                            c++
                    }, 0L, 1L)
                }

                Particle.BLOCK, Particle.FALLING_DUST -> {
                    val material =
                        if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                    if(!material.isBlock) return
                    val blockData = try {
                        material.createBlockData()
                    } catch (_: Exception) {
                        Material.STONE.createBlockData() // Fallback to a guaranteed-safe block
                    }

                    var c = 0
                    EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                        if(c == duration){
                            task.cancel()
                            return@runTimer
                        }
                        if(players == null) {
                            location.world!!.spawnParticle(
                                particle, location,
                                if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                dX, dY, dZ, extra, blockData, force
                            )
                        }else{
                            players.forEach {
                                it.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, blockData)
                            }
                        }
                        c++
                    }, 0L, 1L)
                }

                Particle.ITEM -> {
                    val material =
                        if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE

                    var c = 0
                    EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                        if(c == duration){
                            task.cancel()
                            return@runTimer
                        }
                        if(players == null) {
                            location.world!!.spawnParticle(
                                particle, location,
                                if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                dX, dY, dZ, extra, ItemStack(material), force
                            )
                        }else{
                            players.forEach {
                                it.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, ItemStack(material))
                            }
                        }
                        c++
                    }, 0L, 1L)
                }

                else -> {
                    var c = 0
                    EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                        if(c == duration){
                            task.cancel()
                            return@runTimer
                        }
                        if(players == null) {
                            location.world!!.spawnParticle(
                                particle, location,
                                if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                dX, dY, dZ, extra, null, force
                            )
                        }else{
                            players.forEach {
                                it.spawnParticle(particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, null)
                            }
                        }
                        c++
                    }, 0L, 1L)
                }
            }
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Emitter with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The particle you entered doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    override fun getIdentifier(): String {
        return "PARTICLE_EMITTER"
    }

    override fun getDisplayMaterial(): Material {
        return Material.DISPENSER
    }

    override fun getDescription(): String {
        return "Emits particles over a specific time span."
    }

    override fun isSync(): Boolean {
        return false
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
            "Location",
            "world, 0, 0, 0",
            DefaultDescriptions.LOCATION,
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
            "Duration",
            20,
            DefaultDescriptions.DURATION,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        list.add(Parameter(
            "StartUp",
            0,
            "The time it takes to display the full amount of particles. Set to 0 to disable it.",
            {it.toInt()},
            { it.toFloatOrNull() != null && it.toFloat() >= 0.0 })
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