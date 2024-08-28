package me.m64diamondstar.effectmaster.shows.listeners

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.NamespacedKey
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.persistence.PersistentDataType

class EntityChangeBlockListener: Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockChange(event: EntityChangeBlockEvent){
        if(event.entity !is FallingBlock) return
        if(!ShowUtils.getFallingBlocks().contains(event.entity)) return
        if(!event.entity.persistentDataContainer.has(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"), PersistentDataType.BOOLEAN)) return
        if(event.entity.persistentDataContainer.get(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"), PersistentDataType.BOOLEAN) == false) return

        event.isCancelled = true
        event.entity.remove()
        ShowUtils.removeFallingBlock(event.entity as FallingBlock)
    }

}