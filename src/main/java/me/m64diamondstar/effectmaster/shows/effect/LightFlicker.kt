package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class LightFlicker: Effect() {

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
            val maxLightLevel = if (getSection(effectShow, id).get("MaxLightLevel") != null) getSection(effectShow, id).getInt("MaxLightLevel") else 15
            val minLightLevel = if (getSection(effectShow, id).get("MinLightLevel") != null) getSection(effectShow, id).getInt("MinLightLevel") else 15
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0
            val timeOn = if (getSection(effectShow, id).get("TimeOn") != null) getSection(effectShow, id).getInt("TimeOn") else 20
            val timeOff = if (getSection(effectShow, id).get("TimeOff") != null) getSection(effectShow, id).getInt("TimeOff") else 20

            var c = 0L
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                if(c == duration) {
                    for (player in if(players != null && EffectMaster.isProtocolLibLoaded) players else Bukkit.getOnlinePlayers()) {
                        player.sendBlockChange(location, Material.AIR.createBlockData())
                    }
                    task.cancel()
                    return@runTimer
                }

                val cycle = (timeOn + timeOff).takeIf { it > 0 } ?: 1
                val tickInCycle = c % cycle
                val level = if (tickInCycle < timeOn) maxLightLevel else minLightLevel


                for (player in if(players != null && EffectMaster.isProtocolLibLoaded) players else Bukkit.getOnlinePlayers()) {
                    player.sendBlockChange(location, material.createBlockData("[level=$level]"))
                }

                c++
            }, 0L, 1L)

        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Light Pulse with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
        }

    }

    override fun getIdentifier(): String {
        return "LIGHT_FLICKER"
    }

    override fun getDisplayMaterial(): Material {
        return Material.REDSTONE_LAMP
    }

    override fun getDescription(): String {
        return "Spawn a flickering light source."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(Parameter(
            "Location",
            "world, 0, 0, 0",
            DefaultDescriptions.LOCATION,
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "MaxLightLevel",
            15,
            "The maximum light level.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() in 0..15 })
        )
        list.add(Parameter(
            "MinLightLevel",
            0,
            "The minimum light level.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() in 0..15 })
        )
        list.add(Parameter(
            "TimeOn",
            20,
            "Amount of time the light will be on.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "TimeOff",
            20,
            "Amount of time the light will be off.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Duration",
            200,
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