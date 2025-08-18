package me.m64diamondstar.effectmaster.utils

import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.tag.Modifying
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object CustomTags {

    private class SmallCapsTag: Modifying {

        val alphabet = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".toCharArray()

        override fun apply(current: Component, depth: Int): Component {
            if (depth != 0) return Component.empty()
            if (current !is TextComponent) return current

            val chars = current.content().toCharArray()
            for (i in chars.indices) {
                val ch = chars[i]
                if (ch in 'a'..'z') {
                    chars[i] = alphabet[ch - 'a']
                }
            }

            return current.toBuilder()
                .content(String(chars))
                .mapChildren { comp -> apply(comp, 0) as BuildableComponent<*, *> }
                .build()
        }

    }

    /**
     * Tag resolver for MiniMessage. Can be used by typing
     * {@code <tiny:'some text'>} which will convert the text {@code some text} into tiny caps
     */
    val smallCapsTag: TagResolver = TagResolver.resolver("tiny", SmallCapsTag())
}