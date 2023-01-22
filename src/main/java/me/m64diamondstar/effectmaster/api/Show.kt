package me.m64diamondstar.effectmaster.api

import me.m64diamondstar.effectmaster.shows.utils.EffectShow

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
        val effectShow = EffectShow(category, name)
        effectShow.play()
    }

    /**
     * Play a show, starting at the given ID.
     * @return whether the ID exists or not.
     */
    fun playFrom(id: Int): Boolean{
        val effectShow = EffectShow(category, name)
        return effectShow.playFrom(id)
    }

    /**
     * Only play the given effect ID of a show.
     * @return whether the ID exists or not.
     */
    fun playOnly(id: Int): Boolean{
        val effectShow = EffectShow(category, name)
        return effectShow.playOnly(id)
    }

}