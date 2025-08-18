package me.m64diamondstar.effectmaster.utils

import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.tag.Modifying
import net.kyori.adventure.text.minimessage.tag.Tag
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

    val smallCapsTag: TagResolver = TagResolver.resolver("tiny", SmallCapsTag())

    val successTag: TagResolver = TagResolver.resolver("success", Tag.styling {
        it.color(TextColor.fromHexString(Colors.Color.SUCCESS.toString())).build()
    })

    val errorTag: TagResolver = TagResolver.resolver("error", Tag.styling {
        it.color(TextColor.fromHexString(Colors.Color.ERROR.toString())).build()
    })

    val backgroundTag: TagResolver = TagResolver.resolver("background", Tag.styling {
        it.color(TextColor.fromHexString(Colors.Color.BACKGROUND.toString())).build()
    })

    val defaultTag: TagResolver = TagResolver.resolver("default", Tag.styling {
        it.color(TextColor.fromHexString(Colors.Color.DEFAULT.toString())).build()
    })

    val primaryBlueTag: TagResolver = TagResolver.resolver("primary_blue", Tag.styling {
        it.color(TextColor.fromHexString(Colors.Color.PRIMARY_BLUE.toString())).build()
    })

    val primaryPurpleTag: TagResolver = TagResolver.resolver("primary_purple", Tag.styling {
        it.color(TextColor.fromHexString(Colors.Color.PRIMARY_PURPLE.toString())).build()
    })

    val prefixTag: TagResolver = TagResolver.resolver("prefix", Tag.styling {
        it.color(TextColor.fromHexString(Prefix.PrefixType.DEFAULT.toString())).build()
    })

    val shortPrefixTag: TagResolver = TagResolver.resolver("short_prefix", Tag.styling {
        it.color(TextColor.fromHexString(Prefix.PrefixType.DEFAULT.toShortString())).build()
    })


}