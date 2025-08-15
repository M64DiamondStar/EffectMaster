package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterValidator
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class SoundEffect() : Effect() {

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
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Sound Effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The selector entered is not valid.")
        }

    }

    override fun getIdentifier(): String {
        return "SOUND_EFFECT"
    }

    override fun getDisplayMaterial(): Material {
        return Material.MUSIC_DISC_CAT
    }

    override fun getDescription(): String {
        return "Plays a single sound."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
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
        list.add(Parameter("Sound", "minecraft:entity.pig.ambient", "The sound to play.", {it.lowercase()}, { true }))
        list.add(Parameter(
            "SoundSource",
            "AMBIENT",
            "The source of the sound to play.",
            {it.uppercase()},
            { SoundCategory.entries.firstOrNull { category -> category.name == it } != null})
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
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}