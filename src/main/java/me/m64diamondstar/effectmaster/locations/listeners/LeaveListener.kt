package me.m64diamondstar.effectmaster.locations.listeners

import me.m64diamondstar.effectmaster.locations.LocationsEditor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class LeaveListener: Listener {

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent){
        val player = event.player

        LocationsEditor.remove(player)
    }

}