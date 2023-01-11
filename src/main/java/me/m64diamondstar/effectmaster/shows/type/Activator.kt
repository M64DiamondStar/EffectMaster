package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.Show
import org.bukkit.Bukkit
import org.bukkit.Material

/**
 * Spawns a redstone torch for the given amount of time on a specific location.
 */
class Activator(show: Show, id: Int) : Effect(show, id) {
    override fun execute() {
        val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
        val duration = if (getSection().get("Duration") != null) getSection().getLong("Duration") else 0

        location.block.type = Material.REDSTONE_TORCH

        Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
            location.block.type = Material.AIR
        }, duration)
    }

    override fun getType(): Type {
        return Type.ACTIVATOR
    }

    override fun isSync(): Boolean {
        return true
    }

}