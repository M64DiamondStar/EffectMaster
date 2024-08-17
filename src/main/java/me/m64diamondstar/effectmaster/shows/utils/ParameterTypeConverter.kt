package me.m64diamondstar.effectmaster.shows.utils

fun interface ParameterTypeConverter {
    fun getAsType(value: String): Any
}