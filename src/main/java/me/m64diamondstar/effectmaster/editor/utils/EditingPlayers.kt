package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.entity.Player
import java.util.UUID

object EditingPlayers {

    private val players = HashMap<UUID, Triple<EffectShow, Int, String>>()

    fun add(player: Player, effectShow: EffectShow, id: Int, parameter: String){
        players[player.uniqueId] = Triple(effectShow, id, parameter)
    }

    fun remove(player: Player){
        players.remove(player.uniqueId)
    }

    fun get(player: Player): Triple<EffectShow, Int, String>? {
        return players[player.uniqueId]
    }

    fun contains(player: Player): Boolean{
        return players.contains(player.uniqueId)
    }

}