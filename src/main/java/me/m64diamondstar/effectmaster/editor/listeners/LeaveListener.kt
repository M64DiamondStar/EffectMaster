package me.m64diamondstar.effectmaster.editor.listeners

import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class LeaveListener: Listener {

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent){
        val player = event.player

        EditingPlayers.remove(player)
    }

}