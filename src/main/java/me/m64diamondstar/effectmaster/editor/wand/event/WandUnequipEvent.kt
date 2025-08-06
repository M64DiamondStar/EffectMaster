package me.m64diamondstar.effectmaster.editor.wand.event

import me.m64diamondstar.effectmaster.editor.wand.Wand
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WandUnequipEvent(val player: Player, val wand: Wand): Event() {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        //I just added this.
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

}