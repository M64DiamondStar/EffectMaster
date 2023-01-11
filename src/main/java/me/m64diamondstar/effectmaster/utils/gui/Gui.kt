package me.m64diamondstar.effectmaster.utils.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


/**
    Defines all GUIs in this plugin
 **/
abstract class Gui(private val player: Player) : InventoryHolder{

    private lateinit var inventory: Inventory

    fun getPlayer(): Player{
        return this.player
    }

    override fun getInventory(): Inventory{
        return this.inventory
    }

    abstract fun setDisplayName(): String

    abstract fun setSize(): Int

    abstract fun handleInventory(event: InventoryClickEvent)

    abstract fun setInventoryItems()

    fun open(){
        this.inventory = Bukkit.createInventory(this, setSize(), setDisplayName())
        setInventoryItems()
        player.openInventory(inventory)
    }


}