package me.m64diamondstar.effectmaster.utils

class Prefix {

    companion object {
        val prefix = Colors.format("#c428fb&lE#c12af6&lf#be2df1&lf#bb2fec&le#b832e7&lc#b534e2&lt" +
                " #b237dd&lM#af39d8&la#ac3cd3&ls#a93ece&lt#a641c9&le#a343c4&lr #858585» &f")
        val shortPrefix = Colors.format("#c428fb&lE#c12af6&lM #858585» &f")
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