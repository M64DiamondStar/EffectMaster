package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.entity.Player
import java.util.UUID

object SettingsPlayers {

    private val players = HashMap<UUID, Pair<EffectShow, String>>()

    fun add(player: Player, effectShow: EffectShow, setting: String){
        players[player.uniqueId] = Pair(effectShow, setting)
    }

    fun remove(player: Player){
        players.remove(player.uniqueId)
    }

    fun get(player: Player): Pair<EffectShow, String>? {
        return players[player.uniqueId]
    }

    fun contains(player: Player): Boolean {
        return players.containsKey(player.uniqueId)
    }

}