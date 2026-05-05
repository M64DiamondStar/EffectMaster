package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.calculatePolygonalChain
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.InvalidParameterException
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class BlockLine : Effect() {

    private fun spawnBlock(effectShow: EffectShow, id: Int, location: Location, blockData: BlockData, duration: Long, players: List<Player>?) {
        val normalBlock = location.block

        if(players != null && EffectMaster.isProtocolLibLoaded){
            players.forEach { it.sendBlockChange(location, blockData) }
            effectShow.runLater(id, { _ ->
                players.forEach { it.sendBlockChange(location, normalBlock.blockData) }
            }, duration)
        }else{
            for (player in Bukkit.getOnlinePlayers())
                player.sendBlockChange(location, blockData)
            effectShow.runLater(id, { _ ->
                for (player in Bukkit.getOnlinePlayers())
                    player.sendBlockChange(location, normalBlock.blockData)
            }, duration)
        }
    }

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        val fromLocation =
            if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("FromLocation")
                    ?: throw InvalidParameterException("The FromLocation parameter is null or invalid."),
                    effectShow.centerLocation ?: return)
                    ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
            }else
                LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")
                    ?: throw InvalidParameterException("The FromLocation parameter is null or invalid.")) ?: return
        val toLocation =
            if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("ToLocation")
                    ?: throw InvalidParameterException("The ToLocation parameter is null or invalid."),
                    effectShow.centerLocation ?: return)
                    ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
            }else
                LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return
        val material = if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
            getSection(effectShow, id).getString("Block")!!.uppercase()
        ) else Material.STONE

        if(!material.isBlock) throw InvalidParameterException("The Block parameter is null or invalid.")

        val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0
        val blockData = if(getSection(effectShow, id).get("BlockData") != null)
            try {
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!)
            } catch (_: IllegalArgumentException) {
                throw InvalidParameterException("The Block parameter is null or invalid.")
            }
        else material.createBlockData()

        val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05

        if (speed <= 0) throw InvalidParameterException("The Speed parameter can't be smaller than or equal to 0.")
        if (duration < 0) throw InvalidParameterException("The Duration parameter can't be smaller than 0.")

        val distance = fromLocation.distance(toLocation)

        // How long the effect is expected to last.
        val expectedDuration = distance / speed

        var c = 0.0
        effectShow.runTimer(id, { task ->
            if (c >= 1) {
                task.cancel()
                return@runTimer
            }

            if (expectedDuration / distance < 1) {
                val blocksPerTick = (1 - expectedDuration / distance) * 10
                for (i in 1..blocksPerTick.toInt()) {
                    val progress = c + 1.0 / expectedDuration / blocksPerTick * i
                    if(progress > 1) continue
                    spawnBlock(
                        effectShow, id,
                        calculatePolygonalChain(
                            listOf(fromLocation, toLocation),
                            c + 1.0 / expectedDuration / blocksPerTick * i
                        ), blockData, duration, players
                    )
                }
            }else
                spawnBlock(effectShow, id, calculatePolygonalChain(listOf(fromLocation, toLocation), c), blockData, duration, players)

            c += 1.0 / expectedDuration
        }, 1L, 1L)
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

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
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
        list.add(SuggestingParameter(
            "Block",
            "STONE",
            DefaultDescriptions.BLOCK,
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } },
            Material.entries.filter { it.isBlock }.map { it.name.lowercase() })
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