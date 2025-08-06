package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object GuiItems {

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
     * @return a scroll further item
     */
    fun getScrollFurther(): ItemStack{
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#74c0db&lScroll Further"))
        item.itemMeta = meta
        return item
    }

    /**
     * @return a scroll back item
     */
    fun getScrollBack(): ItemStack{
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#74c0db&lScroll Back"))
        item.itemMeta = meta
        return item
    }

    /**
     * @return the play button
     */
    fun getPlay(): ItemStack{
        val item = ItemStack(Material.EMERALD)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#6cc46d&lPlay"))
        item.itemMeta = meta
        return item
    }

    /**
     * @return
     */
    fun getBack(): ItemStack{
        val item = ItemStack(Material.SPECTRAL_ARROW)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#bd4d4d&lBack"))
        item.itemMeta = meta
        return item
    }

    /**
     * @return delete button
     */
    fun getDelete(): ItemStack{
        val item = ItemStack(Material.BARRIER)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#bd4d4d&lDelete"))
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    /**
     * @return create effect button
     */
    fun getCreateEffect(): ItemStack {
        val item = ItemStack(Material.SLIME_BALL)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#906bcf&lNew Effect"))
        meta.setEnchantmentGlintOverride(true)
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Click to create a new effect!"))
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        item.itemMeta = meta
        return item
    }

    /**
     * @return duplicate effect button
     */
    fun getDuplicate(): ItemStack {
        val item = ItemStack(Material.ENDER_EYE)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#906bcf&lDuplicate"))
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    /**
     * @return button to view all the effects in a show
     */
    fun getViewAll(): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#906bcf&lView All Effects"))
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    /**
     * @return an invalid effect
     */
    fun getInvalidEffect(): ItemStack{
        val item = ItemStack(Material.BARRIER)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#bd4d4d&lInvalid Effect"))
        meta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "This effect was likely an external effect,"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "or a typo has been made in the show file."),
            Colors.format(Colors.Color.BACKGROUND.toString() + "This effect will get skipped when"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "the show is played."))
        meta.persistentDataContainer.set(NamespacedKey(EffectMaster.plugin(), "invalid"), PersistentDataType.BOOLEAN, true)
        item.itemMeta = meta
        return item
    }

    /**
     * @return an item which opens the settings menu of a show
     */
    fun getSettings(): ItemStack {
        val item = ItemStack(Material.FIREWORK_STAR)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#878787&lSettings"))
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

}