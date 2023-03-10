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

        override fun isPossible(arg: String): Boolean {
            return arg.toDoubleOrNull() != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg.toDouble()
        }
    },
    INT{
        override fun getExample(): String {
            return "50"
        }

        override fun isPossible(arg: String): Boolean {
            return arg.toIntOrNull() != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg.toInt()
        }
    },
    BOOLEAN{
        override fun getExample(): String {
            return "false"
        }

        override fun isPossible(arg: String): Boolean {
            return arg.toBooleanStrictOrNull() != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg.toBoolean()
        }
    },
    LOCATION{
        override fun getExample(): String {
            return "false"
        }

        override fun isPossible(arg: String): Boolean {
            return LocationUtils.getLocationFromString(arg) != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg
        }
    },
    COLOR{
        override fun getExample(): String {
            return "188, 94, 219"
        }

        override fun isPossible(arg: String): Boolean {
            return Colors.getJavaColorFromString(arg) != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg
        }
    },
    VECTOR{
        override fun getExample(): String {
            return "1.1, 1.3, 2"
        }

        override fun isPossible(arg: String): Boolean {
            return LocationUtils.getVectorFromString(arg) != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg
        }
    },
    MATERIAL{
        override fun getExample(): String {
            return "STONE"
        }

        override fun isPossible(arg: String): Boolean {
            return Material.values().firstOrNull { it.name == arg } != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg.uppercase()
        }
    },
    PARTICLE{
        override fun getExample(): String {
            return "FLAME"
        }

        override fun isPossible(arg: String): Boolean {
            return Particle.values().firstOrNull { it.name == arg } != null
        }

        override fun convertToFormat(arg: String): Any {
            return arg.uppercase()
        }
    },
    ALL{
        override fun getExample(): String {
            return "this_can_be_everything"
        }

        override fun isPossible(arg: String): Boolean {
            return true
        }

        override fun convertToFormat(arg: String): Any {
            return arg
        }
    };

    abstract fun getExample(): String

    abstract fun isPossible(arg: String): Boolean

    abstract fun convertToFormat(arg: String): Any

}