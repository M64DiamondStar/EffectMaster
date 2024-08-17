package me.m64diamondstar.effectmaster.utils

import java.io.Serializable

data class Pair<out A, out B>(
    val first: A,
    val second: B
) : Serializable
