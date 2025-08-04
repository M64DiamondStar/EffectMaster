package me.m64diamondstar.effectmaster.editor.wand.event

import me.m64diamondstar.effectmaster.editor.wand.Wand
import me.m64diamondstar.effectmaster.editor.wand.WandMode
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WandEquipEvent(val player: Player, val wand: Wand, val wandMode: WandMode) : Event() {

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
