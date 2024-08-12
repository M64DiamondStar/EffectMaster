package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class SoundEffect() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        try {
            val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
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
                val minecartCommand = location.world?.spawnEntity(location, EntityType.MINECART_COMMAND)
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

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "SOUND_EFFECT"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Selector", "null"))
        list.add(Pair("Sound", "minecraft:entity.pig.ambient"))
        list.add(Pair("SoundSource", "AMBIENT"))
        list.add(Pair("Volume", 1))
        list.add(Pair("Pitch", 1))
        list.add(Pair("Delay", 0))
        return list
    }
}