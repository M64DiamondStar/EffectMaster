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

class ReplaceFill() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return
            val material =
                if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
                    getSection(effectShow, id).getString("Block")!!.uppercase()
                ) else Material.STONE

            if(!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Replace Fill with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0
            val replacing = if (getSection(effectShow, id).get("Replacing") != null) Material.valueOf(
                getSection(effectShow, id).getString("Replacing")!!.uppercase()
            ) else Material.COBBLESTONE

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

                        // Check if the current location is the replacing block type
                        if(location.block.type == replacing) {
                            if (players != null) { // Send for specific players
                                players.forEach {
                                    it.sendBlockChange(location, blockData)
                                }
                            } else { // Send for all players
                                for (player in Bukkit.getOnlinePlayers())
                                    player.sendBlockChange(location, blockData)
                            }
                            normalMap[location] = location.block.blockData
                        }
                    }
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                if (players != null) {
                    players.forEach {
                        for (loc in normalMap.keys)
                            it.sendBlockChange(loc, normalMap[loc]!!)
                    }
                } else {
                    for (player in Bukkit.getOnlinePlayers())
                        for (loc in normalMap.keys)
                            player.sendBlockChange(loc, normalMap[loc]!!)
                }
            }, duration)
        } catch (_: IllegalArgumentException) {
            EffectMaster.plugin().logger.warning("Couldn't play Replace Fill with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }
    }

    override fun getIdentifier(): String {
        return "REPLACE_FILL"
    }

    override fun getDisplayMaterial(): Material {
        return Material.GRANITE
    }

    override fun getDescription(): String {
        return "Replaces blocks with another type in a cubic area."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "REPLACE_FILL"))
        list.add(Pair("FromLocation", "world, 0, 0, 0"))
        list.add(Pair("ToLocation", "world, 3, 3, 3"))
        list.add(Pair("Block", "STONE"))
        list.add(Pair("Replacing", "COBBLESTONE"))
        list.add(Pair("Duration", 100))
        list.add(Pair("Delay", 0))
        return list
    }
}