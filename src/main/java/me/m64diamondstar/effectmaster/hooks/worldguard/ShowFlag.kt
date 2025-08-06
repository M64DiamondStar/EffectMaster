package me.m64diamondstar.effectmaster.hooks.worldguard

import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.FlagContext
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat

class ShowFlag(name: String): Flag<Pair<String, String>>(name) {

    override fun parseInput(context: FlagContext?): Pair<String, String>? {
        val input = context?.userInput // Format "show_category show_name"
        if (input.isNullOrEmpty()) {
            throw InvalidFlagFormat("Input cannot be null or empty. Expected format: 'show_category show_name'")
        }
        val parts = input.split(" ")
        if (parts.size < 2) {
            throw InvalidFlagFormat("Expected format: 'show_category show_name'") // Not enough parts to parse
        }

        val showCategory = parts[0]
        val showName = parts[1]

        // Create and return the EffectShow object
        return Pair(
            showCategory,
            showName
        )
    }

    override fun unmarshal(o: Any?): Pair<String, String>? {
        if (o !is Map<*, *>) {
            return null // Invalid type
        }
        val showCategory = o["show_category"] as? String ?: return null
        val showName = o["show_name"] as? String ?: return null

        // Create and return the EffectShow object
        return Pair(
            showCategory,
            showName
        )
    }

    override fun marshal(o: Pair<String, String>?): Any? {
        if (o == null) {
            return null
        }
        val map = mutableMapOf<String, Any>()
        map["show_category"] = o.first
        map["show_name"] = o.second
        return map
    }

}