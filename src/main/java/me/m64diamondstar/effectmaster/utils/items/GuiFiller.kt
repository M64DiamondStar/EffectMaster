package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object GuiFiller {

    /**
     * @return a clean ItemStack with Material BLACK_STAINED_GLASS_PANE
     */
    fun getBlackPane(): ItemStack{
        val item = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.WHITE}")
        item.itemMeta = meta
        return item
    }

    /**
     * @return a clean ItemStack with Material BLACK_STAINED_GLASS_PANE
     */
    fun getGrayPane(): ItemStack{
        val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.WHITE}")
        item.itemMeta = meta
        return item
    }

    /**
     * @return a clean ItemStack with Material RED_STAINED_GLASS_PANE
     */
    fun getRedPane(): ItemStack{
        val item = ItemStack(Material.RED_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.WHITE}")
        item.itemMeta = meta
        return item
    }

    /**
     * @return a clean ItemStack with Material GREEN_STAINED_GLASS_PANE
     */
    fun getGreenPane(): ItemStack{
        val item = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.WHITE}")
        item.itemMeta = meta
        return item
    }

    /**
     * @return a next page item
     */
    fun getScrollFurther(): ItemStack{
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#74c0db&lScroll Further"))
        item.itemMeta = meta
        return item
    }

    /**
     * @return a previous page item
     */
    fun getScrollBack(): ItemStack{
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#74c0db&lScroll Back"))
        item.itemMeta = meta
        return item
    }

    /**
     * @return a previous page item
     */
    fun getPlay(): ItemStack{
        val item = ItemStack(Material.EMERALD)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#6cc46d&lPlay"))
        item.itemMeta = meta
        return item
    }

}