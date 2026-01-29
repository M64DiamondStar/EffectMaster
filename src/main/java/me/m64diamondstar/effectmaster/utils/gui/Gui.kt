package me.m64diamondstar.effectmaster.utils.gui

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.sync
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

    companion object {
        private val cancelHandlePlayers = mutableListOf<Player>()

        fun isCancelingHandle(player: Player) = cancelHandlePlayers.contains(player)

        fun closeInventoryWithoutHandle(player: Player){
            sync {
                cancelHandlePlayers.add(player)
                player.closeInventory()
                cancelHandlePlayers.remove(player)
            }
        }
    }

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
        Bukkit.getGlobalRegionScheduler().run(EffectMaster.plugin()) {
            cancelHandlePlayers.add(player)
            this.inventory = Bukkit.createInventory(this, setSize(), setDisplayName())
            setInventoryItems()
            player.openInventory(inventory)
            cancelHandlePlayers.remove(player)
        }
    }

}