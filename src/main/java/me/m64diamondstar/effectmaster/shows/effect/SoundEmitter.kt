package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.ParameterValidator
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Registry
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class SoundEmitter : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        try {
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val sound = getSection(effectShow, id).getString("Sound") ?: return
            val selector = getSection(effectShow, id).getString("Selector")
            val source = getSection(effectShow, id).getString("SoundSource") ?: return
            val volume = getSection(effectShow, id).getDouble("Volume").toFloat()
            val pitch = getSection(effectShow, id).getDouble("Pitch").toFloat()
            val duration = getSection(effectShow, id).getDouble("Duration").toLong()
            val interval = getSection(effectShow, id).getDouble("Interval").toLong()

            val amount = duration / interval

            var c = 0
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                if(c >= amount){
                    task.cancel()
                    return@runTimer
                }

                if (selector == null || selector.equals("null", ignoreCase = true) || selector.isEmpty())
                    if (players != null) {
                        players.forEach {
                            it.playSound(it, sound, SoundCategory.valueOf(source), volume, pitch)
                        }
                    }
                    else
                        location.world?.playSound(location, sound, SoundCategory.valueOf(source), volume, pitch)

                else {
                    val minecartCommand = location.world?.spawnEntity(location, EntityType.COMMAND_BLOCK_MINECART)
                    EffectMaster.plugin().server.selectEntities(minecartCommand as CommandSender, selector).forEach {
                        if (it is Player)
                            if (players != null) {
                                if (players.contains(it)) {
                                    it.playSound(it, sound, SoundCategory.valueOf(source), volume, pitch)
                                }
                            }
                            else
                                it.playSound(it, sound, SoundCategory.valueOf(source), volume, pitch)
                    }
                    minecartCommand.remove()
                }

                c++
            }, 0L, interval)

        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Sound Emitter with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The selector entered is not valid.")
        }

    }

    override fun getIdentifier(): String {
        return "SOUND_EMITTER"
    }

    override fun getDisplayMaterial(): Material {
        return Material.JUKEBOX
    }

    override fun getDescription(): String {
        return "Emits a sound."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(Parameter("Location", "location", "The location from where the sound is played.", {it}, { true }))
        list.add(Parameter("Selector", "null", "The selector of the players to play the sound to.", {it}, object : ParameterValidator {
            override fun isValid(value: String): Boolean {
                if(value.startsWith("@e") || value.startsWith("@s"))
                    return false
                return try {
                    EffectMaster.plugin().server.selectEntities(Bukkit.getConsoleSender(), value)
                    true
                } catch (_: IllegalArgumentException) {
                    false
                }
            }
        }))
        list.add(SuggestingParameter(
            "Sound",
            "minecraft:entity.pig.ambient",
            "The sound to play.",
            {it.lowercase()},
            { true },
            Registry.SOUNDS.map { it.toString().lowercase() })
        )
        list.add(SuggestingParameter(
            "SoundSource",
            "AMBIENT",
            "The source of the sound to play.",
            {it.uppercase()},
            { SoundCategory.entries.any { mat -> it.equals(mat.name, ignoreCase = true) }},
            SoundCategory.entries.map { it.name.lowercase() })
        )
        list.add(Parameter(
            "Volume",
            1f,
            "The volume of the sound. This is value must me greater than 0",
            {it.toFloat()},
            { it.toFloatOrNull() != null && it.toFloat() >= 0f })
        )
        list.add(Parameter(
            "Pitch",
            1f,
            "The pitch of the sound. This is value must be between 0 and 2",
            {it.toFloat()},
            { it.toFloatOrNull() != null && it.toFloat() in 0f..2f })
        )
        list.add(Parameter(
            "Duration",
            40,
            DefaultDescriptions.DURATION,
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Interval",
            5,
            "The time between each sound in minecraft ticks (20 ticks = 1 second)",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
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