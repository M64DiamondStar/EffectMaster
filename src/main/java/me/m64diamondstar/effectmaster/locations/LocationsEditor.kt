package me.m64diamondstar.effectmaster.locations

import me.m64diamondstar.effectmaster.utils.items.LocationItems
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

object LocationsEditor {

    private val players = HashMap<UUID, Array<ItemStack>>()

    fun add(player: Player){
        players[player.uniqueId] = player.inventory.contents
        player.inventory.contents = emptyArray()

        player.inventory.setItem(0, LocationItems.getAddPoint())
        player.inventory.setItem(1, LocationItems.getRemovePoint())

        player.inventory.setItem(3, LocationItems.getEnter())

        player.inventory.setItem(4, LocationItems.getChangeX())
        player.inventory.setItem(5, LocationItems.getChangeY())
        player.inventory.setItem(6, LocationItems.getChangeZ())

        player.inventory.setItem(8, LocationItems.getCancel())
    }

    fun contains(player: Player): Boolean{
        return players.containsKey(player.uniqueId)
    }

    fun remove(player: Player){
        if(players.containsKey(player.uniqueId)) {
            player.inventory.contents = players[player.uniqueId]!!
            players.remove(player.uniqueId)
        }
    }

}