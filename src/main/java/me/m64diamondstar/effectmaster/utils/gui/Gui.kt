package me.m64diamondstar.effectmaster.utils.gui

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


/**
    Defines all GUIs in this plugin
 **/
abstract class Gui(private val player: Player) : InventoryHolder{

    private lateinit var inventory: Inventory

    override fun getInventory(): Inventory{
        return this.inventory
    }

    abstract fun setDisplayName(): String

    abstract fun setSize(): Int

    abstract fun handleInventory(event: InventoryClickEvent)

    open fun handleClose(event: InventoryCloseEvent) {}

    abstract fun setInventoryItems()

    fun open(){
        EffectMaster.getFoliaLib().scheduler.runNextTick { _ ->
            this.inventory = Bukkit.createInventory(this, setSize(), setDisplayName())
            setInventoryItems()
            player.openInventory(inventory)
        }
    }


}