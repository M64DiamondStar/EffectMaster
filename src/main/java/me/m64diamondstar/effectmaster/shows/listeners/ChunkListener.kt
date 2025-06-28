package me.m64diamondstar.effectmaster.shows.listeners

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import org.bukkit.persistence.PersistentDataType

class ChunkListener: Listener {

    @EventHandler
    fun onEntitiesUnload(event: EntitiesUnloadEvent){
        for(entity in event.entities){
            if ((entity.type == EntityType.ITEM || entity.type == EntityType.FALLING_BLOCK)
                && (ShowUtils.containsDroppedItem(entity.uniqueId) || ShowUtils.containsFallingBlock(entity.uniqueId))) {
                entity.remove()
            }
        }
    }

    @EventHandler
    fun onEntitiesUnload(event: EntitiesLoadEvent){
        for(entity in event.entities){
            if ((entity.type == EntityType.ITEM || entity.type == EntityType.FALLING_BLOCK)
                && (entity.persistentDataContainer.has(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"), PersistentDataType.BOOLEAN))) {
                entity.remove()
            }
        }
    }

}