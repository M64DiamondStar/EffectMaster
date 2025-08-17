package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionType

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
     * @return a scroll further item
     */
    fun getNextEffect(): ItemStack{
        val item = ItemStack(Material.TIPPED_ARROW)
        val meta = item.itemMeta!! as PotionMeta
        meta.setDisplayName(Colors.format("#74c0db&lNext Effect"))
        meta.clearCustomEffects()
        meta.basePotionType = PotionType.WATER
        meta.color = Color.GREEN
        item.itemMeta = meta
        return item
    }

    /**
     * @return a scroll back item
     */
    fun getPreviousEffect(): ItemStack{
        val item = ItemStack(Material.TIPPED_ARROW)
        val meta = item.itemMeta!! as PotionMeta
        meta.setDisplayName(Colors.format("#74c0db&lPrevious Effect"))
        meta.clearCustomEffects()
        meta.basePotionType = PotionType.WATER
        meta.color = Color.RED
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

    /**
     * @return a sorting item with a list of sorting items
     * @param items the list of different sorting types
     * @param selected the selected index of the items list
     */
    fun getSorting(items: List<String>, selected: Int = 0): ItemStack {
        val item = ItemStack(Material.HOPPER)
        val meta = item.itemMeta!!
        meta.setDisplayName(Colors.format("#c8d0e0&lSorting"))
        meta.lore = items.mapIndexed { i, item ->
            Colors.format(if(i == selected) "&n#ffffff$item" else "#a3a3a3$item")
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    /**
     * @return the ItemStack for an Effect item
     */
    fun createEffectItem(effectShow: EffectShow, id: Int): ItemStack {
        val effect = effectShow.getEffect(id)
        return if (effect == null) {
            getInvalidEffect()
        } else {
            ItemStack(effect.getDisplayMaterial()).apply {
                itemMeta = itemMeta?.also { meta ->
                    meta.setDisplayName(
                        Colors.format("#dcb5ff&l${effect.getIdentifier().lowercase()
                            .replace("_", " ")
                            .replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: $id")
                    )
                    val lore = mutableListOf<String>().apply {
                        add(" ")
                        effect.getDefaults().forEach { param ->
                            val value = effect.getSection(effectShow, id)
                                .get(param.name).toString().takeIf { it.isNotBlank() } ?: "N/A"
                            add(Colors.format("&r#e0e0e0&o${Colors.Color.BACKGROUND}${param.name}: ${Colors.Color.DEFAULT}$value"))
                        }
                        add(" ")
                        add(Colors.format("${Colors.Color.SUCCESS}Click to edit!"))
                    }
                    meta.lore = lore
                    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                }
            }
        }
    }

}