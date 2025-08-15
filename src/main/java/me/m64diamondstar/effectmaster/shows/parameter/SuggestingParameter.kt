package me.m64diamondstar.effectmaster.shows.parameter

import java.io.Serializable

data class SuggestingParameter(
    override val name: String,
    override val defaultValue: Any,
    override val description: String,
    override val parameterTypeConverter: ParameterTypeConverter,
    override val parameterValidator: ParameterValidator,

    val suggestionList: List<String>
) : ParameterLike, Serializable

