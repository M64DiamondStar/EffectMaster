package me.m64diamondstar.effectmaster.shows.utils

object DefaultDescriptions {
    const val DELAY = "The delay between the start of the show, and the start of this effect in minecraft ticks (1 second = 20 ticks)"
    const val DURATION = "The duration of the effect in minecraft ticks (1 second = 20 ticks)."
    const val LOCATION = "The location where the effect is going to be played."
    const val BLOCK_DATA = "The block data to use on the block, use [] to leave it empty."
    const val BLOCK = "The block to spawn."
    const val VELOCITY = "The velocity of the effect."
    const val PARTICLE = "The particle to spawn."
    const val LIGHT_LEVEL = "The light level to use, this is a value between 0 and 15."
    const val SPLINE_TYPE = "The type of spline or curve to use. You can choose POLY_CHAIN for a straight line between points, BEZIER for an extremely smooth curve" +
            " that doesn't go through all points and CATMULL_ROM for a somewhat smooth curve which does go through all points."
}