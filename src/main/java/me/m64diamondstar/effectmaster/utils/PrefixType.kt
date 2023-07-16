package me.m64diamondstar.effectmaster.utils

import me.m64diamondstar.effectmaster.EffectMaster

class Prefix {

    companion object {
        val prefix = EffectMaster.plugin.config.getString("messages.prefix.normal")!!
        val shortPrefix = EffectMaster.plugin.config.getString("messages.prefix.short")!!
    }

    enum class PrefixType {
        ERROR {
            override fun toString(): String {
                return Colors.format("$prefix#bd4d4d")
            }

            override fun toShortString(): String {
                return Colors.format("$shortPrefix#bd4d4d")
            }
        },
        SUCCESS {
            override fun toString(): String {
                return Colors.format("$prefix#53bd4d")
            }

            override fun toShortString(): String {
                return Colors.format("$shortPrefix#53bd4d")
            }
        },
        STANDARD {
            override fun toString(): String {
                return Colors.format("$prefix#bfbfbf")
            }

            override fun toShortString(): String {
                return Colors.format("$shortPrefix#bfbfbf")
            }
        },
        BACKGROUND {
            override fun toString(): String {
                return Colors.format("$prefix#7d7d7d")
            }

            override fun toShortString(): String {
                return Colors.format("$shortPrefix#7d7d7d")
            }
        };

        abstract override fun toString(): String
        abstract fun toShortString(): String
    }



}