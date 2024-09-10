package me.m64diamondstar.effectmaster.shows.listeners

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import org.bukkit.persistence.PersistentDataType

class ChunkListener: Listener {

    @EventHandler
    fun onChunkUnload(event: ChunkUnloadEvent){
        val chunk = event.chunk

        val droppedItems = ShowUtils.getDroppedItems()
        val fallingBlocks = ShowUtils.getFallingBlocks()

        for(entity in chunk.entities){
            if ((entity.type == EntityType.DROPPED_ITEM || entity.type == EntityType.FALLING_BLOCK)
                && (entity in droppedItems || entity in fallingBlocks)) {
                entity.remove()
            }
        }
    }

    @EventHandler
    fun onEntitiesUnload(event: EntitiesUnloadEvent){
        for(entity in event.entities){
            if ((entity.type == EntityType.DROPPED_ITEM || entity.type == EntityType.FALLING_BLOCK)
                && (entity in ShowUtils.getDroppedItems() || entity in ShowUtils.getFallingBlocks())) {
                entity.remove()
            }
        }
    }

    @EventHandler
    fun onEntitiesUnload(event: EntitiesLoadEvent){
        for(entity in event.entities){
            if ((entity.type == EntityType.DROPPED_ITEM || entity.type == EntityType.FALLING_BLOCK)
                && (entity.persistentDataContainer.has(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"), PersistentDataType.BOOLEAN))) {
                entity.remove()
            }
        }
    }

}