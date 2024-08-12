package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class SetBlock() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        try {
            val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val material =
                if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE

            if(!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Set Block with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0
            val real = if (getSection(effectShow, id).get("Real") != null) getSection(effectShow, id).getBoolean("Real") else false
            val normalBlock = location.block
            val normalBlockType = location.block.type
            val normalBlockData = location.block.blockData

            if (real) {
                location.block.type = material

                Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                    location.block.type = normalBlockType
                    location.block.blockData = normalBlockData
                }, duration)
            } else {
                if(players != null && EffectMaster.isProtocolLibLoaded){
                    players.forEach { it.sendBlockChange(location, blockData) }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                        players.forEach { it.sendBlockChange(location, normalBlock.blockData) }
                    }, duration)
                }else{
                    for (player in Bukkit.getOnlinePlayers())
                        player.sendBlockChange(location, material.createBlockData())
                    Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                        for (player in Bukkit.getOnlinePlayers())
                            player.sendBlockChange(location, normalBlock.blockData)
                    }, duration)
                }
            }
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Set Block with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }

    }

    override fun getIdentifier(): String {
        return "SET_BLOCK"
    }

    override fun getDisplayMaterial(): Material {
        return Material.STONE
    }

    override fun getDescription(): String {
        return "Sets a single block."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "SET_BLOCK"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Location", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Block", "STONE"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("BlockData", "[]"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Duration", 100))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Real", false))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}