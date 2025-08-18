package me.m64diamondstar.effectmaster.ktx

import org.bukkit.Location
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

fun PlayerInteractEvent.isLeftClick(): Boolean {
    return this.action == Action.LEFT_CLICK_AIR || this.action == Action.LEFT_CLICK_BLOCK
}

fun PlayerInteractEvent.isRightClick(): Boolean {
    return this.action == Action.RIGHT_CLICK_AIR || this.action == Action.RIGHT_CLICK_BLOCK
}

fun PlayerInteractEvent.clickedBlockLocation(): Location? {
    val clickedBlock = this.clickedBlock ?: return null
    val clickedPosition = this.clickedPosition ?: return null
    return clickedBlock.location.clone().add(clickedPosition)
}