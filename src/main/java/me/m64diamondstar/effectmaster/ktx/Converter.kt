package me.m64diamondstar.effectmaster.ktx

import me.m64diamondstar.effectmaster.utils.Quadruple
import org.bukkit.Material

/**
 * This is used to convert a sequencer which features 3 dimensions and a material into a sequencer with just the dimensions.
 * @return A map with 3 dimensions x, y and z for each index
 */
fun Map<Int, Quadruple<Double?, Double?, Double?, Material?>>?.toTriple(): Map<Int, Triple<Double?, Double?, Double?>>? {
    return this?.mapValues { (_, value) ->
        Triple(value.first, value.second, value.third)
    }
}

/**
 * This is used to convert a sequencer which features 2 dimensions and a material into a sequencer with just the dimensions.
 * @return A map with 2 dimensions width and depth for each index
 */
fun Map<Int, Triple<Double?, Double?, Material?>>?.toPair(): Map<Int, Pair<Double?, Double?>>? {
    return this?.mapValues { (_, value) ->
        Pair(value.first, value.second)
    }
}

/**
 * Get a Triple of Doubles from a string in the format double1, double2, double3.
 * @return a Triple of Double
 */
fun tripleDoubleFromString(string: String): Triple<Double, Double, Double>? {
    val parts = string.split(",").map { it.trim() }
    if (parts.size != 3) return null

    return try {
        val first = parts[0].toDouble()
        val second = parts[1].toDouble()
        val third = parts[2].toDouble()
        Triple(first, second, third)
    } catch (_: NumberFormatException) {
        null
    }
}