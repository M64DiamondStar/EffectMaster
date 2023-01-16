package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Material
import org.bukkit.Particle

enum class ParameterFormatType {

    DOUBLE{
        override fun getExample(): String {
            return "3.94"
        }

        override fun isPossible(arg: Any): Boolean {
            return arg is Double
        }
    },
    INT{
        override fun getExample(): String {
            return "50"
        }

        override fun isPossible(arg: Any): Boolean {
            return arg is Int
        }
    },
    BOOLEAN{
        override fun getExample(): String {
            return "false"
        }

        override fun isPossible(arg: Any): Boolean {
            return arg is Boolean
        }
    },
    LOCATION{
        override fun getExample(): String {
            return "false"
        }

        override fun isPossible(arg: Any): Boolean {
            if(arg !is String) return false
            return LocationUtils.getLocationFromString(arg) != null
        }
    },
    COLOR{
        override fun getExample(): String {
            return "188, 94, 219"
        }

        override fun isPossible(arg: Any): Boolean {
            if(arg !is String) return false
            return Colors.getJavaColorFromString(arg) != null
        }
    },
    VECTOR{
        override fun getExample(): String {
            return "1.1, 1.3, 2"
        }

        override fun isPossible(arg: Any): Boolean {
            if(arg !is String) return false
            return LocationUtils.getVectorFromString(arg) != null
        }
    },
    MATERIAL{
        override fun getExample(): String {
            return "STONE"
        }

        override fun isPossible(arg: Any): Boolean {
            if(arg !is String) return false
            return Material.values().firstOrNull { it.name == arg } != null
        }
    },
    PARTICLE{
        override fun getExample(): String {
            return "FLAME"
        }

        override fun isPossible(arg: Any): Boolean {
            if(arg !is String) return false
            return Particle.values().firstOrNull { it.name == arg } != null
        }
    },
    ALL{
        override fun getExample(): String {
            return "this_can_be_everything"
        }

        override fun isPossible(arg: Any): Boolean {
            return true
        }
    };

    abstract fun getExample(): String

    abstract fun isPossible(arg: Any): Boolean

}