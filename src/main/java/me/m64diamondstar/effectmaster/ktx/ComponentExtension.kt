package me.m64diamondstar.effectmaster.ktx

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)
