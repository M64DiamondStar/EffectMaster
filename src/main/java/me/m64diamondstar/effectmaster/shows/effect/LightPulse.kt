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
import kotlin.math.roundToInt
import kotlin.math.sin

class LightPulse: Effect() {

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
            val wavelength = if (getSection(effectShow, id).get("Wavelength") != null) getSection(effectShow, id).getInt("Wavelength") else 0

            var c = 0L
            effectShow.runTimer(id, { task ->
                if(c == duration) {
                    for (player in if(players != null && EffectMaster.isProtocolLibLoaded) players else Bukkit.getOnlinePlayers()) {
                        player.sendBlockChange(location, Material.AIR.createBlockData())
                    }
                    task.cancel()
                    return@runTimer
                }

                val amplitude = (maxLightLevel - minLightLevel) / 2.0
                val midpoint = (maxLightLevel + minLightLevel) / 2.0
                val level = (sin(c * Math.PI / wavelength) * amplitude + midpoint).roundToInt().coerceIn(minLightLevel, maxLightLevel)

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
        return "LIGHT_PULSE"
    }

    override fun getDisplayMaterial(): Material {
        return Material.SEA_LANTERN
    }

    override fun getDescription(): String {
        return "Spawn a pulsing light source."
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
            1,
            "The minimum light level.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() in 0..15 })
        )
        list.add(Parameter(
            "Wavelength",
            20,
            "Time it takes for the light to turn on and back off in ticks.",
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