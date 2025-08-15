package me.m64diamondstar.effectmaster.shows.parameter

interface ParameterLike {
    /**
     * The name of the parameter.
     */
    val name: String

    /**
     * The default value to set.
     */
    val defaultValue: Any

    /**
     * A short description to explain what the parameter does.
     */
    val description: String

    /**
     * Sets the parameter to the correct type. All inputs are always String, but not every parameter should be a string. This method will set the parameter value to the correct type/class.
     */
    val parameterTypeConverter: ParameterTypeConverter

    /**
     * A checker to verify if the parameter entered by a user is valid.
     */
    val parameterValidator: ParameterValidator
}