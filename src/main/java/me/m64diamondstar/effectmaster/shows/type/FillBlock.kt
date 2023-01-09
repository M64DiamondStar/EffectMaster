package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.EffectType
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData

class FillBlock(show: Show, private val id: Int) : EffectType(show, id) {

    override fun execute() {
        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection().getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection().getString("ToLocation")!!) ?: return
            val material =
                if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
            val duration = if (getSection().get("Duration") != null) getSection().getLong("Duration") else 0

            val normalMap = HashMap<Location, BlockData>()

            for (x in fromLocation.blockX.coerceAtLeast(toLocation.blockX) downTo toLocation.blockX.coerceAtMost(
                fromLocation.blockX
            )) {
                for (y in fromLocation.blockY.coerceAtLeast(toLocation.blockY) downTo toLocation.blockY.coerceAtMost(
                    fromLocation.blockY
                )) {
                    for (z in fromLocation.blockZ.coerceAtLeast(toLocation.blockZ) downTo toLocation.blockZ.coerceAtMost(
                        fromLocation.blockZ
                    )) {
                        val location = Location(fromLocation.world, x.toDouble(), y.toDouble(), z.toDouble())
                        for (player in Bukkit.getOnlinePlayers())
                            player.sendBlockChange(location, material.createBlockData())
                        normalMap[location] = location.block.blockData
                    }
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
                for (player in Bukkit.getOnlinePlayers())
                    for (loc in normalMap.keys)
                        player.sendBlockChange(loc, normalMap[loc]!!)
            }, duration)
        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The block you entered doesn't exist. Please choose a valid material.")
        }
    }

    override fun getType(): Types {
        return Types.FILL_BLOCK
    }

    override fun isSync(): Boolean {
        return true
    }
}