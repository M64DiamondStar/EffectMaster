package me.m64diamondstar.effectmaster.hooks

import com.sk89q.worldguard.WorldGuard
import me.m64diamondstar.effectmaster.hooks.worldguard.ShowDelayFlag
import me.m64diamondstar.effectmaster.hooks.worldguard.ShowFlag

class WorldGuardManager {

    private val flagRegistry = WorldGuard.getInstance().flagRegistry

    // Plays the set show every time a player enters or exits a region.
    val enterFlag = ShowFlag("em-enter")
    val exitFlag = ShowFlag("em-exit")

    // Plays the set show only once when a player first enters or last exits a region.
    val firstEnterFlag = ShowFlag("em-first-enter")
    val lastExitFlag = ShowFlag("em-last-exit")

    // Plays the set show with a delay when a player enters or exits a region.
    // If the player isn't in the region anymore when the delay is over, the show won't play.
    val delayEnterFlag = ShowDelayFlag("em-delay-enter")
    val delayExitFlag = ShowDelayFlag("em-delay-exit")

    // Plays the set show with a delay when a player first enters or last exits a region.
    // If the player isn't in the region anymore when the delay is over, the show won't
    val delayFirstEnterFlag = ShowDelayFlag("em-delay-first-enter")
    val delayLastExitFlag = ShowDelayFlag("em-delay-last-exit")

    init {
        flagRegistry.registerAll(listOf(
            enterFlag, exitFlag,
            firstEnterFlag, lastExitFlag,
            delayEnterFlag, delayExitFlag,
            delayFirstEnterFlag, delayLastExitFlag))
    }

}