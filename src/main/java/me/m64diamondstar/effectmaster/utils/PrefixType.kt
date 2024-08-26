package me.m64diamondstar.effectmaster.utils

import me.m64diamondstar.effectmaster.EffectMaster

class Prefix {

    companion object {
        fun getPrefix(): String{
            return EffectMaster.plugin().config.getString("messages.prefix.normal")!!
        }

        fun getShortPrefix(): String{
            return EffectMaster.plugin().config.getString("messages.prefix.short")!!
        }
    }

    enum class PrefixType {
        ERROR {
            override fun toString(): String {
                return Colors.format("${getPrefix()}#bd4d4d")
            }

            override fun toShortString(): String {
                return Colors.format("${getShortPrefix()}#bd4d4d")
            }
        },
        SUCCESS {
            override fun toString(): String {
                return Colors.format("${getPrefix()}#53bd4d")
            }

            override fun toShortString(): String {
                return Colors.format("${getShortPrefix()}#53bd4d")
            }
        },
        DEFAULT {
            override fun toString(): String {
                return Colors.format("${getPrefix()}#bfbfbf")
            }

            override fun toShortString(): String {
                return Colors.format("${getShortPrefix()}#bfbfbf")
            }
        },
        BACKGROUND {
            override fun toString(): String {
                return Colors.format("${getPrefix()}#7d7d7d")
            }

            override fun toShortString(): String {
                return Colors.format("${getShortPrefix()}#7d7d7d")
            }
        },
        WHITE {
            override fun toString(): String {
                return Colors.format("${getPrefix()}#e8e8e8")
            }

            override fun toShortString(): String {
                return Colors.format("${getShortPrefix()}#e8e8e8")
            }
        };

        abstract override fun toString(): String
        abstract fun toShortString(): String
    }

}