package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
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

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "ACTIVATOR"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Location", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Duration", 5))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }

}