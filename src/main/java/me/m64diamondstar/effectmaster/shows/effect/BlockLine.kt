package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.calculatePolygonalChain
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class BlockLine() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val fromLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("FromLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("ToLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return
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


            val distance = fromLocation.distance(toLocation)

            // How long the effect is expected to last.
            val expectedDuration = distance / speed

            var c = 0.0
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                if (c >= 1) {
                    task.cancel()
                    return@runTimer
                }

                if (expectedDuration / distance < 1) {
                    val blocksPerTick = (1 - expectedDuration / distance) * 10
                    for (i in 1..blocksPerTick.toInt())
                        spawnBlock(calculatePolygonalChain(listOf(fromLocation, toLocation), c + 1.0 / expectedDuration / blocksPerTick * i), blockData, duration, players)
                }else
                    spawnBlock(calculatePolygonalChain(listOf(fromLocation, toLocation), c), blockData, duration, players)

                c += 1.0 / expectedDuration
            }, 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Line with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    private fun spawnBlock(location: Location, blockData: BlockData, duration: Long, players: List<Player>?) {
        val normalBlock = location.block

        if(players != null && EffectMaster.isProtocolLibLoaded){
            players.forEach { it.sendBlockChange(location, blockData) }
            EffectMaster.getFoliaLib().scheduler.runLater({ task ->
                players.forEach { it.sendBlockChange(location, normalBlock.blockData) }
            }, duration)
        }else{
            for (player in Bukkit.getOnlinePlayers())
                player.sendBlockChange(location, blockData)
            EffectMaster.getFoliaLib().scheduler.runLater({ task ->
                for (player in Bukkit.getOnlinePlayers())
                    player.sendBlockChange(location, normalBlock.blockData)
            }, duration)
        }
    }

    override fun getIdentifier(): String {
        return "BLOCK_LINE"
    }

    override fun getDisplayMaterial(): Material {
        return Material.CHISELED_STONE_BRICKS
    }

    override fun getDescription(): String {
        return "Spawns blocks between two locations."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter(
            "FromLocation",
            "world, 0, 0, 0",
            "The location where the block line starts.",
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "ToLocation",
            "world, 1, 1, 1",
            "The location where the block line ends.",
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "Block",
            "STONE",
            DefaultDescriptions.BLOCK,
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        )
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, {it}, { true }))
        list.add(Parameter(
            "Speed",
            1,
            "The speed of the block line progression. Measured in blocks/second.",
            {it.toDouble()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Duration",
            40,
            "How long each block should stay visible.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}