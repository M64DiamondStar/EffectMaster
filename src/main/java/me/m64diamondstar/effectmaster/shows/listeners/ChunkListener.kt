package me.m64diamondstar.effectmaster.shows.listeners

import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent

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

}