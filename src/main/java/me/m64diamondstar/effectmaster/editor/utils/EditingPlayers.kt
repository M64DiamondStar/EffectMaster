package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.shows.utils.Show
import org.bukkit.entity.Player

object EditingPlayers {

    private val players = HashMap<Player, Triple<Show, Int, String>>()

    fun add(player: Player, show: Show, id: Int, parameter: String){
        players[player] = Triple(show, id, parameter)
    }

    fun remove(player: Player){
        players.remove(player)
    }

    fun get(player: Player): Triple<Show, Int, String>? {
        return players[player]
    }

    fun contains(player: Player): Boolean{
        return players.contains(player)
    }

}