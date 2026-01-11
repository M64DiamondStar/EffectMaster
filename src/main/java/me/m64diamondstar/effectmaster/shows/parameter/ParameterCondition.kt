package me.m64diamondstar.effectmaster.shows.parameter

fun interface ParameterCondition {
    fun condition(parameters: Map<ParameterLike, String>): Boolean
}