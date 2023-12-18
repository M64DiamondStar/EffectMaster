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

class BlockLine(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {

        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection().getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection().getString("ToLocation")!!) ?: return
            val material = if (getSection().get("Block") != null) Material.valueOf(
                getSection().getString("Block")!!.uppercase()
            ) else Material.STONE

            if(!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val duration = if (getSection().get("Duration") != null) getSection().getLong("Duration") else 0
            val blockData = if(getSection().get("BlockData") != null)
                Bukkit.createBlockData(material, getSection().getString("BlockData")!!) else material.createBlockData()
            val speed = if (getSection().get("Speed") != null) getSection().getDouble("Speed") * 0.05 else 0.05

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
                return
            }


            val distance = fromLocation.distance(toLocation)

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
                            spawnBlock(LocationUtils.calculatePolygonalChain(listOf(fromLocation, toLocation), c + 1.0 / expectedDuration / blocksPerTick * i), blockData, duration, players)
                    }else
                        spawnBlock(LocationUtils.calculatePolygonalChain(listOf(fromLocation, toLocation), c), blockData, duration, players)

                    c += 1.0 / expectedDuration
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (ex: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Line with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
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

    override fun getType(): Type {
        return Type.BLOCK_LINE
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "BLOCK_LINE"))
        list.add(Pair("FromLocation", "world, 0, 0, 0"))
        list.add(Pair("ToLocation", "world, 0, 3, 0"))
        list.add(Pair("Block", "BLUE_STAINED_GLASS"))
        list.add(Pair("BlockData", "[]"))
        list.add(Pair("Speed", 1))
        list.add(Pair("Duration", 40))
        list.add(Pair("Delay", 0))
        return list
    }
}