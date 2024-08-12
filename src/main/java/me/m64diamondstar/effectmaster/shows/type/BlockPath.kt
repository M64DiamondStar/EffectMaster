package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class BlockPath() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val path = LocationUtils.getLocationPathFromString(getSection(effectShow, id).getString("Path")!!)
            if(path.size < 2) return
            val material = if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
                getSection(effectShow, id).getString("Block")!!.uppercase()
            ) else Material.STONE

            if(!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0
            val blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
                return
            }

            val smooth = if (getSection(effectShow, id).get("Smooth") != null) getSection(effectShow, id).getBoolean("Smooth") else true

            var distance = 0.0
            for(loc in 1 until path.size){
                distance += path[loc - 1].distance(path[loc])
            }

            // How long the effect is expected to last.
            val expectedDuration = distance / speed

            object : BukkitRunnable() {
                var c = 0.0
                override fun run() {
                    if (c >= 1) {
                        cancel()
                        return
                    }

                    if (expectedDuration / distance < 1) {
                        val blocksPerTick = (1 - expectedDuration / distance) * 10
                        for (i in 1..blocksPerTick.toInt())
                            if(smooth)
                                spawnBlock(LocationUtils.calculateBezierPoint(path, c + 1.0 / expectedDuration / blocksPerTick * i), blockData, duration, players)
                            else
                                spawnBlock(LocationUtils.calculatePolygonalChain(path, c + 1.0 / expectedDuration / blocksPerTick * i), blockData, duration, players)
                    }else{
                        if (smooth)
                            spawnBlock(LocationUtils.calculateBezierPoint(path, c), blockData, duration, players)
                        else
                            spawnBlock(LocationUtils.calculatePolygonalChain(path, c), blockData, duration, players)
                    }

                    c += 1.0 / expectedDuration
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }
    }

    private fun spawnBlock(location: Location, blockData: BlockData, duration: Long, players: List<Player>?) {
        val normalBlock = location.block

        if(players != null && EffectMaster.isProtocolLibLoaded){
            players.forEach { it.sendBlockChange(location, blockData) }
            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                players.forEach { it.sendBlockChange(location, normalBlock.blockData) }
            }, duration)
        }else{
            for (player in Bukkit.getOnlinePlayers())
                player.sendBlockChange(location, blockData)
            Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
                for (player in Bukkit.getOnlinePlayers())
                    player.sendBlockChange(location, normalBlock.blockData)
            }, duration)
        }
    }

    override fun getIdentifier(): String {
        return "BLOCK_PATH"
    }

    override fun getDisplayMaterial(): Material {
        return Material.SMOOTH_STONE
    }

    override fun getDescription(): String {
        return "Spawns a path of blocks."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "BLOCK_PATH"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Path", "world, 0, 0, 0; 3, 3, 3"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Block", "BLUE_STAINED_GLASS"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("BlockData", "[]"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Speed", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Duration", 40))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Smooth", true))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}