package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.entity.Player

object EditingPlayers {

    private val players = HashMap<Player, Triple<EffectShow, Int, String>>()

    fun add(player: Player, effectShow: EffectShow, id: Int, parameter: String){
        players[player] = Triple(effectShow, id, parameter)
    }

    fun remove(player: Player){
        players.remove(player)
    }

    fun get(player: Player): Triple<EffectShow, Int, String>? {
        return players[player]
    }

    fun contains(player: Player): Boolean{
        return players.contains(player)
    }

}