package me.m64diamondstar.effectmaster.editor.utils

import io.papermc.paper.event.player.AsyncChatEvent
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.ktx.plainText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ChatSession {

    private val players = mutableMapOf<Player, ChatValueSession>()

    /**
     * Prompt a player to write a message in chat.
     * @param player the player.
     * @param prompt the question as a component, this will get sent in chat.
     * @param validator checks whether the entered value is allowed (for disallowing spaces etc.).
     * @param onComplete runs when the entered value is validated.
     * @param onCancel runs when the session gets cancelled.
     * @param tabCompletions is an optional list of tab completions for the player.
     */
    fun prompt(
        player: Player,
        prompt: Component,
        validator: Validator,
        onInvalid: ((String) -> Unit)? = null,
        onComplete: (String) -> Unit,
        onCancel: () -> Unit = {},
        tabCompletions: List<String> = emptyList()
    ) {
        players[player] = ChatValueSession(prompt, validator, onInvalid, onComplete, onCancel, tabCompletions)
        player.addCustomChatCompletions(tabCompletions)
        player.sendMessage(prompt)
        player.sendMessage(emComponent(
            "<error>" +
                    "<click:run_command:/em cancel>" +
                    "<hover:show_text:Or click this text to cancel.>" +
                    "To cancel this session, please type <i>cancel."
        ))
    }

    /**
     * @return whether the entered player is currently in a session.
     */
    fun isPrompted(player: Player): Boolean {
        return players.containsKey(player)
    }

    /**
     * Enter a value for a player. If the player isn't in a session, nothing will happen.
     */
    fun enter(player: Player, value: String) {
        val session = players[player] ?: return
        if(value.equals("cancel", ignoreCase = true)) {
            cancel(player)
            return
        }

        if (!session.validator.isValid(value)) {
            if(session.onInvalid == null)
                player.sendMessage(emComponent("<error>Invalid, please try again:"))
            else
                session.onInvalid(value)
            return
        }

        // Entered value is valid
        player.removeCustomChatCompletions(session.tabCompletions)
        players.remove(player)

        // Run onComplete last to make chaining sessions possible
        session.onComplete(value)
    }

    /**
     * Cancel a session for a player. If the player isn't in a session, nothing will happen.
     */
    fun cancel(player: Player) {
        val session = players[player] ?: return
        session.onCancel()
        player.removeCustomChatCompletions(session.tabCompletions)
        players.remove(player)
        player.sendMessage(emComponent("<prefix><success>Cancelled session."))
    }

    fun interface Validator {
        fun isValid(string: String): Boolean
    }

    private data class ChatValueSession(
        val prompt: Component,
        val validator: Validator,
        val onInvalid: ((String) -> Unit)? = null,
        val onComplete: (String) -> Unit,
        val onCancel: () -> Unit,
        val tabCompletions: List<String>
    )

    class ChatListener : Listener {

        @EventHandler
        fun onChat(event: AsyncChatEvent) {
            players[event.player] ?: return
            val player = event.player
            val value = event.originalMessage().plainText()

            event.isCancelled = true
            enter(player, value.trim())
        }

    }

}
