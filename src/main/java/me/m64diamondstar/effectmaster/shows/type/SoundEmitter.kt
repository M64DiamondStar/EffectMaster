package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class SoundEmitter(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {
        try {
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
            val sound = getSection().getString("Sound") ?: return
            val selector = getSection().getString("Selector")
            val source = getSection().getString("SoundSource") ?: return
            val volume = getSection().getDouble("Volume").toFloat()
            val pitch = getSection().getDouble("Pitch").toFloat()
            val duration = getSection().getDouble("Duration").toLong()
            val interval = getSection().getDouble("Interval").toLong()

            val amount = duration / interval

            object: BukkitRunnable(){
                var c = 0
                override fun run() {
                    if (selector == null || (selector.equals("null", ignoreCase = true) || selector.isEmpty()))
                        if (players != null) {
                            players.forEach {
                                it.playSound(it, sound, SoundCategory.valueOf(source), volume, pitch)
                            }
                        }
                        else
                            location.world?.playSound(location, sound, SoundCategory.valueOf(source), volume, pitch)

                    else {
                        val minecartCommand = location.world?.spawnEntity(location, EntityType.MINECART_COMMAND)
                        EffectMaster.plugin.server.selectEntities(minecartCommand as CommandSender, selector).forEach {
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
            }.runTaskTimer(EffectMaster.plugin, 0L, interval)

        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play Sound Emitter with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("Possible errors: ")
            EffectMaster.plugin.logger.warning("- The selector entered is not valid.")
        }

    }

    override fun getType(): Type {
        return Type.SOUND_EMITTER
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "SOUND_EMITTER"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Selector", "null"))
        list.add(Pair("Sound", "minecraft:entity.pig.ambient"))
        list.add(Pair("SoundSource", "AMBIENT"))
        list.add(Pair("Volume", 1))
        list.add(Pair("Pitch", 1))
        list.add(Pair("Duration", 20))
        list.add(Pair("Interval", 1))
        list.add(Pair("Delay", 0))
        return list
    }
}