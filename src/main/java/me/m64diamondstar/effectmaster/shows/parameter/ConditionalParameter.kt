package me.m64diamondstar.effectmaster.shows.parameter

data class ConditionalParameter(
    override val name: String,
    override val defaultValue: Any,
    override val description: String,
    override val parameterTypeConverter: ParameterTypeConverter,
    override val parameterValidator: ParameterValidator,
    val parameterCondition: ParameterCondition
) : ParameterLike