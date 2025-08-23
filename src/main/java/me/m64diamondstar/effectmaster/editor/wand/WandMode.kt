package me.m64diamondstar.effectmaster.editor.wand

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent

interface WandMode {

    /**
     * What should happen when the player interacts with the Effect Wand in this mode.
     * @param event the PlayerInteractEvent which gets triggered when the player interacts in this mode.
     */
    fun onInteract(event: PlayerInteractEvent)

    /**
     * Gets called when a player equips this wand mode.
     */
    fun onEquip(player: Player) {}

    /**
     * Gets called when a player un-equips this wand mode.
     */
    fun onUnequip(player: Player) {}

    /**
     * The ID of this mode
     */
    fun getId(): String

    /**
     * The material the wand should change to when it is in this mode.
     */
    fun getMaterial(): Material

    /**
     * The display name of the wand in this mode.
     */
    fun getDisplay(): Component

    /**
     * The description of this mode
     */
    fun getDescription(): List<Action>

    /**
     * The permission the player should have in order to be able to use this mode.
     * Set it to null if everyone is allowed to use it.
     */
    fun getPermission(): String?

    /**
     * This function will run each tick when a player has the wand in its hand on this mode.
     */
    fun task(player: Player)

    data class Action(
        val action: ModeAction,
        val description: String
    )

    enum class ModeAction {
        LEFT_CLICK {
            override fun toString(): String = "Left Click"
        },
        LEFT_CLICK_SHIFT {
            override fun toString(): String = "Left Click + Shift"
        },
        RIGHT_CLICK {
            override fun toString(): String = "Right Click"
        },
        RIGHT_CLICK_SHIFT {
            override fun toString(): String = "Right Click + Shift"
        };

        abstract override fun toString(): String
    }


}