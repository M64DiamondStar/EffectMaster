package me.m64diamondstar.effectmaster.editor.sessions

import org.bukkit.entity.Player
import java.util.UUID

class UserPreferences(val uniqueId: UUID) {

    companion object {

        private val preferences = HashMap<UUID, UserPreferences>()

        fun get(player: Player): UserPreferences {
            return get(player.uniqueId)
        }

        fun get(uniqueId: UUID): UserPreferences {
            return preferences.getOrPut(uniqueId) { UserPreferences(uniqueId) }
        }

        fun remove(uniqueId: UUID) {
            preferences.remove(uniqueId)
        }

    }

    object Defaults {
        const val EFFECT_SORTING = "effect_sorting"
    }

    private val preferences = mutableMapOf<String, Any>()

    fun setPreference(key: String, preference: Any) {
        preferences[key] = preference
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreference(key: String): T? {
        return preferences[key] as? T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreference(key: String, default: T): T {
        return preferences.getOrPut(key) { default as Any } as T
    }

}