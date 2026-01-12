package me.m64diamondstar.effectmaster.shows.utils

import org.bukkit.util.Vector
import kotlin.random.Random

fun Vector.applyRandomizer(randomizer: Double): Vector {
    return if (randomizer != 0.0)
        Vector(
            this.x + (Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer),
            this.y + (Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3),
            this.z + (Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer)
        )
    else
        this
}