package me.m64diamondstar.effectmaster.editor.wand.event

import me.m64diamondstar.effectmaster.editor.wand.Wand
import me.m64diamondstar.effectmaster.editor.wand.WandTasks
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class WandListener: Listener {

    @EventHandler
    fun onWandEquip(event: PlayerItemHeldEvent){
        val player = event.player
        val item = player.inventory.getItem(event.newSlot) ?: return
        val wand = Wand.getWand(item) ?: return
        val wandMode = wand.getMode(item) ?: return

        val equipEvent = WandEquipEvent(player, wand, wandMode)
        Bukkit.getPluginManager().callEvent(equipEvent)
    }

    @EventHandler
    fun onWandUnequip(event: PlayerItemHeldEvent){
        val player = event.player
        val item = player.inventory.getItem(event.previousSlot) ?: return
        val wand = Wand.getWand(item) ?: return

        val unequipEvent = WandUnequipEvent(player, wand)
        Bukkit.getPluginManager().callEvent(unequipEvent)
    }

    @EventHandler
    fun onWandModeSwitch(event: PlayerSwapHandItemsEvent){
        val player = event.player
        val item = event.offHandItem ?: return
        val wand = Wand.getWand(item) ?: return
        val currentMode = wand.getMode(item) ?: return
        val nextMode = wand.getNextMode(item) ?: return

        event.isCancelled = true
        player.inventory.setItemInMainHand(wand.nextMode(item))
        val switchEvent = WandModeSwitchEvent(player, wand, currentMode, nextMode)
        Bukkit.getPluginManager().callEvent(switchEvent)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent){
        val item = event.item
        val wand = Wand.getWand(item) ?: return
        wand.getMode(item)?.onInteract(event)
    }

    @EventHandler
    fun onEquip(event: WandEquipEvent){
        val player = event.player
        val wand = event.wand
        val mode = event.wandMode
        WandTasks.removeHolder(player)
        WandTasks.addHolder(player, wand, mode)
    }

    @EventHandler
    fun onUnequip(event: WandUnequipEvent){
        val player = event.player
        WandTasks.removeHolder(player)
    }

    @EventHandler
    fun onSwitch(event: WandModeSwitchEvent){
        val player = event.player
        val wand = event.wand
        val mode = event.toMode
        WandTasks.removeHolder(player)
        WandTasks.addHolder(player, wand, mode)
    }

}