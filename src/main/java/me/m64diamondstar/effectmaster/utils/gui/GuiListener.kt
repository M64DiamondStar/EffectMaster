package me.m64diamondstar.effectmaster.utils.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent


class GuiListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(event: InventoryClickEvent){
        val holder = event.inventory.holder
        if(holder is Gui) {
            event.isCancelled = true
            if (event.slot == -999) return
            holder.handleInventory(event)
        }
    }
}