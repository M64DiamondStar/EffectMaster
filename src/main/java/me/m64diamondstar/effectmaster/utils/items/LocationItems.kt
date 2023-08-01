package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object LocationItems {

    fun getChangeX(): ItemStack {
        val item = ItemStack(Material.RED_CONCRETE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#a83232±X"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click to increase"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "Shift + Right Click to decrease"))
        item.itemMeta = meta
        return item
    }

    fun getChangeY(): ItemStack {
        val item = ItemStack(Material.GREEN_CONCRETE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#32a852±Y"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click to increase"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "Shift + Right Click to decrease"))
        item.itemMeta = meta
        return item
    }

    fun getChangeZ(): ItemStack {
        val item = ItemStack(Material.BLUE_CONCRETE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#325aa8±Z"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click to increase"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "Shift + Right Click to decrease"))
        item.itemMeta = meta
        return item
    }

    fun getAddPoint(): ItemStack {
        val item = ItemStack(Material.LIME_DYE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format(Colors.Color.SUCCESS.toString() + "Add Point"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click to add a new point"))
        item.itemMeta = meta
        return item
    }

    fun getRemovePoint(): ItemStack {
        val item = ItemStack(Material.GRAY_DYE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format(Colors.Color.ERROR.toString() + "Remove Point"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click to remove the selected point"))
        item.itemMeta = meta
        return item
    }

    fun getEnter(): ItemStack {
        val item = ItemStack(Material.ENDER_EYE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#5fa8e3Enter"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click enter the current location(s)"))
        item.itemMeta = meta
        return item
    }

    fun getCancel(): ItemStack {
        val item = ItemStack(Material.BARRIER)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format(Colors.Color.ERROR.toString() + "Cancel"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Right Click to cancel"))
        item.itemMeta = meta
        return item
    }

}