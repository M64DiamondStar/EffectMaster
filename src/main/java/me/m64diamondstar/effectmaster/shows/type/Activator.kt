package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Spawns a redstone torch for the given amount of time on a specific location.
 */
class Activator() : Effect() {
    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
        val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getLong("Duration") else 0

        location.block.type = Material.REDSTONE_TORCH

        Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin(), {
            location.block.type = Material.AIR
        }, duration)
    }

    override fun getIdentifier(): String {
        return "ACTIVATOR"
    }

    override fun getDisplayMaterial(): Material {
        return Material.REDSTONE_TORCH
    }

    override fun getDescription(): String {
        return "Spawns a redstone torch for the given amount of time on a specific location."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Location", "world, 0, 0, 0", DefaultDescriptions.LOCATION, {it}) { LocationUtils.getLocationFromString(it) != null })
        list.add(Parameter("Duration", 5, DefaultDescriptions.DURATION, {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }

}