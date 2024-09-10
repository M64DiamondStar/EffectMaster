package me.m64diamondstar.effectmaster.shows.utils

import java.io.Serializable

data class ShowSetting(
    /**
     * The name of the parameter.
     */
    val identifier: String,

    /**
     * The default value to set.
     */
    val value: Any
) : Serializable {

    object Identifier {
        const val PLAY_AT = "play-at"
    }

}