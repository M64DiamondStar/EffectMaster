package me.m64diamondstar.effectmaster.ktx

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

/**
 * @return a plain text string using the PlainTextComponentSerializer.
 */
fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)

/**
 * Sets the italic decoration to false. Useful for display names and lore.
 */
fun Component.withoutItalics(): Component = this.decoration(TextDecoration.ITALIC, false)
