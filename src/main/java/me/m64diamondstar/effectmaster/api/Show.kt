package me.m64diamondstar.effectmaster.api

import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.entity.Player

/**
 * Create a show object.
 * The category is the folder the show is in.
 * The name is the name or the show. You can use it with or without the .yml,
 * it will be automatically corrected.
 */
class Show(private val category: String, private val name: String) {

    /**
     * Play a show from start to finish.
     */
    fun play(){
        val effectShow = EffectShow(category, name, null)
        effectShow.play()
    }

    /**
     * Play a show from start to finish for a specific list of players.
     * @param players The list of players that will be able to see the show.
     */
    fun play(players: List<Player>){
        val effectShow = EffectShow(category, name, players)
        effectShow.play()
    }

    /**
     * Play a show from start to finish for a specific player.
     * @param player The player that will be able to see the show.
     */
    fun play(player: Player){
        val effectShow = EffectShow(category, name, listOf(player))
        effectShow.play()
    }

    /**
     * Play a show, starting at the given ID.
     * @return whether the ID exists or not.
     * @param id The effect ID where the show has to start. The given ID will also be included.
     */
    fun playFrom(id: Int): Boolean{
        val effectShow = EffectShow(category, name, null)
        return effectShow.playFrom(id)
    }

    /**
     * Play a show, starting at the given ID.
     * @return whether the ID exists or not.
     * @param id The effect ID where the show has to start. The given ID will also be included.
     * @param players The list of players that will be able to see the show.
     */
    fun playFrom(id: Int, players: List<Player>): Boolean{
        val effectShow = EffectShow(category, name, players)
        return effectShow.playFrom(id)
    }

    /**
     * Play a show, starting at the given ID.
     * @return whether the ID exists or not.
     * @param id The effect ID where the show has to start. The given ID will also be included.
     * @param player The player that will be able to see the show.
     */
    fun playFrom(id: Int, player: Player): Boolean{
        val effectShow = EffectShow(category, name, listOf(player))
        return effectShow.playFrom(id)
    }

    /**
     * Only play the given effect ID of a show.
     * @return whether the ID exists or not.
     * @param id the effect ID that has to be shown.
     */
    fun playOnly(id: Int): Boolean{
        val effectShow = EffectShow(category, name, null)
        return effectShow.playOnly(id)
    }

    /**
     * Only play the given effect ID of a show.
     * @return whether the ID exists or not.
     * @param id the effect ID that has to be shown.
     * @param players The list of players that will be able to see the show.
     */
    fun playOnly(id: Int, players: List<Player>): Boolean{
        val effectShow = EffectShow(category, name, players)
        return effectShow.playOnly(id)
    }

    /**
     * Only play the given effect ID of a show.
     * @return whether the ID exists or not.
     * @param id the effect ID that has to be shown.
     * @param player The player that will be able to see the show.
     */
    fun playOnly(id: Int, player: Player): Boolean{
        val effectShow = EffectShow(category, name, listOf(player))
        return effectShow.playOnly(id)
    }

}