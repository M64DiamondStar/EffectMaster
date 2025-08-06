package me.m64diamondstar.effectmaster.hooks.worldguard

import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.FlagContext
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat

class ShowDelayFlag(name: String): Flag<Triple<String, String, Long>>(name) {

    override fun parseInput(context: FlagContext?): Triple<String, String, Long>? {
        val input = context?.userInput // Format "show_category show_name delay"
        if (input.isNullOrEmpty()) {
            throw InvalidFlagFormat("Input cannot be null or empty. Expected format: 'show_category show_name delay'") // Input is null or empty
        }
        val parts = input.split(" ")
        if (parts.size < 3) {
            throw InvalidFlagFormat("Expected format: 'show_category show_name delay'") // Not enough parts to parse
        }

        val showCategory = parts[0]
        val showName = parts[1]
        val delay = parts[2].toLongOrNull() ?: return null // Parse delay

        // Create and return the EffectShow object
        return Triple(
            showCategory,
            showName,
            delay
        )
    }

    override fun unmarshal(o: Any?): Triple<String, String, Long>? {
        if (o !is Map<*, *>) {
            return null // Invalid type
        }

        val showCategory = o["show_category"] as? String ?: return null
        val showName = o["show_name"] as? String ?: return null

        val delayValue = o["delay"]
        val delay = when (delayValue) {
            is Int -> delayValue.toLong()
            is Long -> delayValue
            is String -> delayValue.toLongOrNull()
            else -> null
        } ?: return null

        return Triple(showCategory, showName, delay)
    }

    override fun marshal(o: Triple<String, String, Long>?): Any? {
        if (o == null) {
            return null
        }
        val map = mutableMapOf<String, Any>()
        map["show_category"] = o.first
        map["show_name"] = o.second
        map["delay"] = o.third
        return map
    }

}