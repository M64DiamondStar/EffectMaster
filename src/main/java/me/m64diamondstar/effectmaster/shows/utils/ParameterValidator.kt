package me.m64diamondstar.effectmaster.shows.utils

fun interface ParameterValidator {
    fun isValid(value: String): Boolean
}