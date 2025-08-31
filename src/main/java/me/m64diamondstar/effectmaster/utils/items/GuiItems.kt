package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.ktx.withoutItalics
import me.m64diamondstar.effectmaster.shows.EffectShow
import net.kyori.adventure.text.Component
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
        meta.displayName(Component.empty())
        item.itemMeta = meta
        return item
    }

    /**
     * @return a clean ItemStack with Material BLACK_STAINED_GLASS_PANE
     */
    fun getGrayPane(): ItemStack{
        val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.displayName(Component.empty())
        item.itemMeta = meta
        return item
    }

    /**
     * @return a clean ItemStack with Material RED_STAINED_GLASS_PANE
     */
    fun getRedPane(): ItemStack{
        val item = ItemStack(Material.RED_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.displayName(Component.empty())
        item.itemMeta = meta
        return item
    }

    /**
     * @return a clean ItemStack with Material GREEN_STAINED_GLASS_PANE
     */
    fun getGreenPane(): ItemStack{
        val item = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
        val meta = item.itemMeta!!
        meta.displayName(Component.empty())
        item.itemMeta = meta
        return item
    }

    /**
     * @return a scroll further item
     */
    fun getScrollFurther(): ItemStack{
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.displayName(emComponent("<primary_blue><tiny><b>scroll further").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to scroll further").withoutItalics()))
        item.itemMeta = meta
        return item
    }

    /**
     * @return a scroll back item
     */
    fun getScrollBack(): ItemStack{
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.displayName(emComponent("<primary_blue><tiny><b>scroll back").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to scroll back").withoutItalics()))
        item.itemMeta = meta
        return item
    }

    /**
     * @return a scroll further item
     */
    fun getNextEffect(): ItemStack{
        val item = ItemStack(Material.TIPPED_ARROW)
        val meta = item.itemMeta!! as PotionMeta
        meta.displayName(emComponent("<primary_blue><tiny><b>next effect").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to navigate to the next effect ID").withoutItalics()))
        meta.clearCustomEffects()
        meta.basePotionType = PotionType.WATER
        meta.color = Color.GREEN
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        item.itemMeta = meta
        return item
    }

    /**
     * @return a scroll back item
     */
    fun getPreviousEffect(): ItemStack{
        val item = ItemStack(Material.TIPPED_ARROW)
        val meta = item.itemMeta!! as PotionMeta
        meta.displayName(emComponent("<primary_blue><tiny><b>previous effect").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to navigate to the previous effect ID").withoutItalics()))
        meta.clearCustomEffects()
        meta.basePotionType = PotionType.WATER
        meta.color = Color.RED
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        item.itemMeta = meta
        return item
    }

    /**
     * @return the play button
     */
    fun getPlay(type: String = "show"): ItemStack{
        val item = ItemStack(Material.EMERALD)
        val meta = item.itemMeta!!
        meta.displayName(emComponent("<success><tiny><b>play $type").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to play the $type").withoutItalics()))
        item.itemMeta = meta
        return item
    }

    /**
     * @return the back button
     */
    fun getBack(): ItemStack{
        val item = ItemStack(Material.SPECTRAL_ARROW)
        val meta = item.itemMeta!!
        meta.displayName(emComponent("<error><tiny><b>back").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to go back to the previous UI").withoutItalics()))
        item.itemMeta = meta
        return item
    }

    /**
     * @return delete button
     */
    fun getDeleteEffect(): ItemStack{
        val item = ItemStack(Material.BARRIER)
        val meta = item.itemMeta!!
        meta.displayName(emComponent("<error><tiny><b>delete").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to delete this effect").withoutItalics()))
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
        meta.displayName(emComponent("<primary_purple><tiny><b>new effect").withoutItalics())
        meta.setEnchantmentGlintOverride(true)
        meta.lore(listOf(emComponent("<background>Click to create a new effect").withoutItalics()))
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
        meta.displayName(emComponent("<primary_purple><tiny><b>duplicate").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to duplicate the effect").withoutItalics()))
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
        meta.displayName(emComponent("<primary_blue><tiny><b>view all effects").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to view all of the effects").withoutItalics()))
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
        meta.displayName(emComponent("<error><tiny><b>invalid effect").withoutItalics())
        meta.lore(listOf(emComponent("<background>This effect was likely an external effect,"),
            emComponent("<background>or a typo has been made in the show file."),
            emComponent("<background>This effect will get skipped when"),
            emComponent("<background>the show is played.")))
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
        meta.displayName(emComponent("<default><tiny><b>settings").withoutItalics())
        meta.lore(listOf(emComponent("<background>Click to open the settings editor").withoutItalics()))
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
        meta.displayName(emComponent("<primary_blue><tiny><b>sorting").withoutItalics())
        meta.lore(items.mapIndexed { i, item ->
            emComponent(if (i == selected) "<u><white>$item" else "<background>$item").withoutItalics()
        })
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    /**
     * @return the ItemStack for an Effect item
     */
    fun getEffectItem(effectShow: EffectShow, id: Int): ItemStack {
        val effect = effectShow.getEffect(id)
        return if (effect == null) {
            getInvalidEffect()
        } else {
            ItemStack(effect.getDisplayMaterial()).apply {
                itemMeta = itemMeta?.also { meta ->
                    meta.displayName(
                        emComponent("<primary_purple><b><tiny>${effect.getIdentifier().lowercase()
                            .replace("_", " ")}</tiny></b>")
                            .withoutItalics()
                            .append(emComponent(" <reset><default>ID: $id")))

                    val lore = mutableListOf<Component>().apply {
                        add(Component.empty())
                        effect.getDefaults().forEach { param ->
                            val value = effect.getSection(effectShow, id)
                                .get(param.name).toString().takeIf { it.isNotBlank() } ?: "N/A"
                            add(emComponent("<reset><#e0e0e0><italic><background>${param.name}: <default>$value").withoutItalics())
                        }
                        add(Component.empty())
                        add(emComponent("<success>Click to edit!").withoutItalics())
                    }
                    meta.lore(lore)
                    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                }
            }
        }
    }

}