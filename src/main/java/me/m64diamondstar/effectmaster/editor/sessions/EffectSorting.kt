package me.m64diamondstar.effectmaster.editor.sessions

enum class EffectSorting {

    SMALLEST_ID {
        override fun toString(): String = "Smallest ID"
    },
    LARGEST_ID {
        override fun toString(): String = "Largest ID"
    },
    DELAY {
        override fun toString(): String = "Delay"
    },
    EFFECT_TYPE {
        override fun toString(): String = "Effect Type"
    };

    fun next(): EffectSorting {
        val values = entries.toTypedArray()
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }

}