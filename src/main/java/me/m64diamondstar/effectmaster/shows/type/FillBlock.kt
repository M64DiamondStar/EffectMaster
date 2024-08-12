package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.locations.LocationUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class FillBlock() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return
            val material =
                if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE

            if(!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0

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
                        if(players != null) {
                            players.forEach {
                                it.sendBlockChange(location, blockData)
                            }
                        }else{
                            for (player in Bukkit.getOnlinePlayers())
                                player.sendBlockChange(location, blockData)
                        }
                        normalMap[location] = location.block.blockData
                    }
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                if (players != null &&  EffectMaster.isProtocolLibLoaded){
                    players.forEach {
                        for (loc in normalMap.keys)
                            it.sendBlockChange(loc, normalMap[loc]!!)
                    }
                }else{
                    for (player in Bukkit.getOnlinePlayers())
                        for (loc in normalMap.keys)
                            player.sendBlockChange(loc, normalMap[loc]!!)
                }
            }, duration)
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Fill Block with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }
    }

    override fun getIdentifier(): String {
        return "FILL_BLOCK"
    }

    override fun getDisplayMaterial(): Material {
        return Material.POLISHED_ANDESITE
    }

    override fun getDescription(): String {
        return "Fill a cubic area with blocks."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "FILL_BLOCK"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("FromLocation", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("ToLocation", "world, 3, 3, 3"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Block", "STONE"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("BlockData", "[]"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Duration", 100))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}