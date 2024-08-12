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
import org.bukkit.scheduler.BukkitRunnable

class SoundEmitter() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        try {
            val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val sound = getSection(effectShow, id).getString("Sound") ?: return
            val selector = getSection(effectShow, id).getString("Selector")
            val source = getSection(effectShow, id).getString("SoundSource") ?: return
            val volume = getSection(effectShow, id).getDouble("Volume").toFloat()
            val pitch = getSection(effectShow, id).getDouble("Pitch").toFloat()
            val duration = getSection(effectShow, id).getDouble("Duration").toLong()
            val interval = getSection(effectShow, id).getDouble("Interval").toLong()

            val amount = duration / interval

            object: BukkitRunnable(){
                var c = 0
                override fun run() {
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

                    c++
                    if(c >= amount){
                        this.cancel()
                        return
                    }
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, interval)

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

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "SOUND_EMITTER"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Location", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Selector", "null"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Sound", "minecraft:entity.pig.ambient"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("SoundSource", "AMBIENT"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Volume", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Pitch", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Duration", 20))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Interval", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}