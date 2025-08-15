package me.m64diamondstar.effectmaster.shows.parameter

fun interface ParameterValidator {
    fun isValid(value: String): Boolean
}