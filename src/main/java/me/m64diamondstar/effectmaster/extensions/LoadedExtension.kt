package me.m64diamondstar.effectmaster.extensions

data class LoadedExtension(
    val name: String,
    val mainClass: String,
    val version: String,
    val authors: List<String>,
    )
