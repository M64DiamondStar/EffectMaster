package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.EffectType
import me.m64diamondstar.effectmaster.utils.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.Show
import org.bukkit.Bukkit
import org.bukkit.Material

class SetBlock(show: Show, id: Int) : EffectType(show, id) {

    override fun execute() {
        val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
        val material =
            if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
        val duration = if (getSection().get("Duration") != null) getSection().getLong("Duration") else 0
        val real = if(getSection().get("Real") != null) getSection().getBoolean("Real") else false
        val normalBlock = location.block

        if(real){
            location.block.type = material

            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
                location.block.type = normalBlock.type
            }, duration)
        }else {
            for (player in Bukkit.getOnlinePlayers())
                player.sendBlockChange(location, material.createBlockData())

            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
                for (player in Bukkit.getOnlinePlayers())
                    player.sendBlockChange(location, normalBlock.blockData)
            }, duration)
        }

    }

    override fun getType(): Types {
        return Types.SET_BLOCK
    }

    override fun isSync(): Boolean {
        return true
    }
}