package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.collections.forEach

class Light: Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        try {
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            if(!location.block.isEmpty) return

            val material = Material.LIGHT
            val lightLevel = if (getSection(effectShow, id).get("LightLevel") != null) getSection(effectShow, id).getInt("LightLevel") else 15
            val blockData = material.createBlockData("[level=$lightLevel]")
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0

            if(players != null && EffectMaster.isProtocolLibLoaded){
                players.forEach { it.sendBlockChange(location, blockData) }
                EffectMaster.getFoliaLib().scheduler.runLater({ task ->
                    players.forEach { it.sendBlockChange(location, Material.AIR.createBlockData()) }
                }, duration)
            }else{
                for (player in Bukkit.getOnlinePlayers())
                    player.sendBlockChange(location, material.createBlockData())
                EffectMaster.getFoliaLib().scheduler.runLater({ task ->
                    for (player in Bukkit.getOnlinePlayers())
                        player.sendBlockChange(location, Material.AIR.createBlockData())
                }, duration)

            }
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Set Block with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }

    }

    override fun getIdentifier(): String {
        return "LIGHT"
    }

    override fun getDisplayMaterial(): Material {
        return Material.GLOWSTONE
    }

    override fun getDescription(): String {
        return "Spawn a light source."
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
            "LightLevel",
            15,
            DefaultDescriptions.LIGHT_LEVEL,
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() in 0..15 })
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