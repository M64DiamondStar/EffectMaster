package me.m64diamondstar.effectmaster.utils.gui

import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent


class GuiListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(event: InventoryClickEvent){
        val holder = event.inventory.holder
        if(holder is Gui) {
            event.isCancelled = true
            if (event.slot == -999) return
            if(event.currentItem == null) return
            holder.handleInventory(event)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        val player = event.player as Player
        if(holder is Gui) {
            if(!Gui.isSwitching(player) && EditingPlayers.get(player) == null && SettingsPlayers.get(player) == null)
                holder.handleClose(event)
        }
    }
}