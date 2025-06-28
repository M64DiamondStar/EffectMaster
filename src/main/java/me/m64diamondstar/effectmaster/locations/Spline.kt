package me.m64diamondstar.effectmaster.locations

import org.bukkit.Location

enum class Spline {

    BEZIER {
        override fun calculate(
            path: List<Location>,
            t: Double
        ): Location {
            return calculateBezierPoint(path, t)
        }
    },
    CATMULL_ROM {
        override fun calculate(
            path: List<Location>,
            t: Double
        ): Location {
            return calculateCatmullRomPoint(path, t)
        }
    },
    POLY_CHAIN {
        override fun calculate(
            path: List<Location>,
            t: Double
        ): Location {
            return calculatePolygonalChain(path, t)
        }
    };


    /**
     * Calculate a specific point of a path
     * @param path the list of locations which define the path
     * @param t a value between 0 and 1 with 0 being the start location and 1 being the end location
     */
    abstract fun calculate(path: List<Location>, t: Double): Location

}