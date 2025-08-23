package me.m64diamondstar.effectmaster.ktx

import me.m64diamondstar.effectmaster.EffectMaster
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

/**
 * Creates a Component from an EffectMaster minimessage string.
 * - `<tiny>text</tiny>` for small caps format
 * - `<success>` for green color
 * - `<error>` for red color
 * - `<default>` for light gray color
 * - `<background>` for dark gray color
 * - `<primary_blue>` for blue color
 * - `<primary_purple>` for purple color
 * - `<prefix>` to insert a prefix
 * - `<short_prefix>` to insert a short prefix
 * @param string the string using MiniMessage format
 */
fun emComponent(string: String): Component = EffectMaster.miniMessage.deserialize(string)