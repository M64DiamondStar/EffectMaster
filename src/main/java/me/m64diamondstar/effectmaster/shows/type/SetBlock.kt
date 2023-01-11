package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.Show
import org.bukkit.Bukkit
import org.bukkit.Material

class SetBlock(show: Show, private val id: Int) : Effect(show, id) {

    override fun execute() {
        try {
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
            val material =
                if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
            val duration = if (getSection().get("Duration") != null) getSection().getLong("Duration") else 0
            val real = if (getSection().get("Real") != null) getSection().getBoolean("Real") else false
            val normalBlock = location.block

            if (real) {
                location.block.type = material

                Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
                    location.block.type = normalBlock.type
                }, duration)
            } else {
                for (player in Bukkit.getOnlinePlayers())
                    player.sendBlockChange(location, material.createBlockData())

                Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
                    for (player in Bukkit.getOnlinePlayers())
                        player.sendBlockChange(location, normalBlock.blockData)
                }, duration)
            }
        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The block you entered doesn't exist. Please choose a valid material.")
        }

    }

    override fun getType(): Type {
        return Type.SET_BLOCK
    }

    override fun isSync(): Boolean {
        return true
    }
}