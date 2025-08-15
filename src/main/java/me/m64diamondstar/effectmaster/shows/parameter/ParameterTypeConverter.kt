package me.m64diamondstar.effectmaster.shows.parameter

fun interface ParameterTypeConverter {
    fun getAsType(value: String): Any
}