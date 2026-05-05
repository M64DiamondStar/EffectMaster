package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.entity.Player
import java.util.UUID

object ShowErrorHandler {

    private val reported = HashMap<String, Long>()
    private val debugPlayers = HashSet<UUID>()

    // How long to suppress duplicate errors (default: 30 seconds)
    private const val COOLDOWN_MS = 30_000L

    fun report(effectShow: EffectShow, id: Int, throwable: Throwable) {
        val key = "${effectShow.getCategory()}/${effectShow.getName()}#$id:${throwable::class.simpleName}:${throwable.message}"
        val now = System.currentTimeMillis()

        if ((now - (reported[key] ?: 0L)) >= COOLDOWN_MS) {
            reported[key] = now
            EffectMaster.plugin().logger.warning(
                "[EffectMaster] Error in show '${effectShow.getCategory()}/${effectShow.getName()}'" +
                        " at effect #$id (${throwable::class.simpleName}): ${throwable.message}" +
                        " — further identical errors suppressed for ${COOLDOWN_MS / 1000}s."
            )

            // Send error report to debug players
            val errorMessage = emComponent("<short_prefix><error>Error in show '<primary_blue>${effectShow.getCategory()}/${effectShow.getName()}<error>' at effect #$id: <primary_purple>${throwable::class.simpleName}<error>: <primary_purple>${throwable.message}")

            debugPlayers.forEach { playerId ->
                val player = EffectMaster.plugin().server.getPlayer(playerId)
                player?.sendMessage(errorMessage)
            }
        }
    }

    /**
     * Returns a list of currently reported errors (for debugging purposes).
     * Each entry is a string key representing the error.
     */
    fun list(): List<String> {
        return reported.keys.toList()
    }

    /** Call this on plugin reload so stale cooldowns don't hide fresh errors. */
    fun clear() = reported.clear()

    /**
     * Add a debug player who will receive error reports in chat.
     * This is intended for testing and should not be used in production.
     */
    fun addDebugPlayer(player: Player) {
        debugPlayers.add(player.uniqueId)
    }

    /**
     * Remove a debug player.
     */
    fun removeDebugPlayer(player: Player) {
        debugPlayers.remove(player.uniqueId)
    }

}