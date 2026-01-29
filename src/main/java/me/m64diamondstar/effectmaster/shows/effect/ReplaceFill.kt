package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class ReplaceFill : Effect() {

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
            val replacingBlockData = if(getSection(effectShow, id).get("ReplacingBlockData") != null)
                Bukkit.createBlockData(replacing, getSection(effectShow, id).getString("ReplacingBlockData")!!) else replacing.createBlockData()

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
                        if(location.block.blockData.matches(replacingBlockData)) {
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

            effectShow.runLater(id, { _ ->
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
        list.add(Parameter(
            "BlockData",
            "[]",
            "The block data of the new blocks. Use [] if you don't want to use specific block data.",
            {it},
            { true })
        )
        list.add(SuggestingParameter(
            "Replacing",
            "DIAMOND_BLOCK",
            "The block that will be replaced.",
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } },
            Material.entries.filter { it.isBlock }.map { it.name.lowercase() })
        )
        list.add(Parameter(
            "ReplacingBlockData",
            "[]",
            "The block data of the replaced block. Use [] if you don't want to use specific block data.",
            {it},
            { true })
        )
        list.add(Parameter(
            "Duration",
            100,
            DefaultDescriptions.DURATION,
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