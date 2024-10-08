package me.m64diamondstar.effectmaster.utils

import net.md_5.bungee.api.ChatColor
import java.awt.Color
import java.util.regex.Pattern

object Colors {
    private val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
    @JvmStatic
    fun format(string: String): String {
        var msg = string
        var match = pattern.matcher(msg)
        while (match.find()) {
            val color = msg.substring(match.start(), match.end())
            msg = msg.replace(color, ChatColor.of(color).toString() + "")
            match = pattern.matcher(msg)
        }
        return ChatColor.translateAlternateColorCodes('&', msg).replace(":gs:", "✪")
    }

    fun getJavaColorFromString(string: String): java.awt.Color? {
        val args = string.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return try {
            val r = args[0].toInt()
            val g = args[1].toInt()
            val b = args[2].toInt()
            Color(r, g, b)
        } catch (_: NumberFormatException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun getBukkitColorList(string: String): List<org.bukkit.Color>{
        val arg = string.split(", ")
        val list = ArrayList<org.bukkit.Color>()

        arg.forEach {
            if(it.matches(pattern.toRegex()))
                list.add(org.bukkit.Color.fromRGB(
                    java.awt.Color.decode(it).red,
                    java.awt.Color.decode(it).green,
                    java.awt.Color.decode(it).blue)
                )
        }
        return list
    }

    fun isColorList(string: String): Boolean {
        val arg = string.split(", ")

        arg.forEach {
            if(!it.matches(pattern.toRegex())) return false
        }
        return true
    }

    enum class Color {
        ERROR{
            override fun toString(): String {
                return "#bd4d4d"
            }
        },
        SUCCESS{
            override fun toString(): String {
                return "#53bd4d"
            }
        },
        DEFAULT{
            override fun toString(): String {
                return "#bfbfbf"
            }
        },
        BACKGROUND{
            override fun toString(): String {
                return "#7d7d7d"
            }
        }
    }
}