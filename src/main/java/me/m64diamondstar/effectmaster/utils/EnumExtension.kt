package me.m64diamondstar.effectmaster.utils

inline fun <reified T : Enum<T>> nextEnumValue(current: T): T {
    val values = enumValues<T>()
    val nextIndex = (current.ordinal + 1) % values.size
    return values[nextIndex]
}