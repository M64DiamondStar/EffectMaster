package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class SetBlock() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        try {
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
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

                EffectMaster.getFoliaLib().scheduler.runLater({ task ->
                    location.block.type = normalBlockType
                    location.block.blockData = normalBlockData
                }, duration)
            } else {
                if(players != null && EffectMaster.isProtocolLibLoaded){
                    players.forEach { it.sendBlockChange(location, blockData) }
                    EffectMaster.getFoliaLib().scheduler.runLater({ task ->
                        players.forEach { it.sendBlockChange(location, normalBlock.blockData) }
                    }, duration)
                }else{
                    for (player in Bukkit.getOnlinePlayers())
                        player.sendBlockChange(location, material.createBlockData())
                    EffectMaster.getFoliaLib().scheduler.runLater({ task ->
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

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter(
            "Location",
            "world, 0, 0, 0",
            DefaultDescriptions.LOCATION,
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
            "Duration",
            100,
            DefaultDescriptions.DURATION,
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Real",
            false,
            "Whether the block is real or not. If not, the block will not be saved in the world as a real block, but it'll disappear if it gets interacted with.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
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