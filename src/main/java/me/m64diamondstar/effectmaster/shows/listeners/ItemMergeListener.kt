package me.m64diamondstar.effectmaster.shows.listeners

import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemMergeEvent

class ItemMergeListener: Listener {

    @EventHandler
    fun onItemMerge(event: ItemMergeEvent){
        if(ShowUtils.containsDroppedItem(event.entity.uniqueId)) {
            event.isCancelled = true
            ShowUtils.removeDroppedItem(event.entity)
        }
    }

}