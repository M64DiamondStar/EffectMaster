package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import org.bukkit.entity.Player
import java.util.UUID

object EditingPlayers {

    private val players = HashMap<UUID, Triple<EffectShow, Int, ParameterLike>>()

    fun add(player: Player, effectShow: EffectShow, id: Int, parameter: ParameterLike){
        players[player.uniqueId] = Triple(effectShow, id, parameter)
        if(parameter is SuggestingParameter) {
            player.addCustomChatCompletions(parameter.suggestionList)
        }
    }

    fun remove(player: Player){
        if(contains(player)){
            val parameter = get(player)?.third
            if(parameter is SuggestingParameter) {
                player.removeCustomChatCompletions(parameter.suggestionList)
            }
        }
        players.remove(player.uniqueId)
    }

    fun get(player: Player): Triple<EffectShow, Int, ParameterLike>? {
        return players[player.uniqueId]
    }

    fun contains(player: Player): Boolean{
        return players.contains(player.uniqueId)
    }

}