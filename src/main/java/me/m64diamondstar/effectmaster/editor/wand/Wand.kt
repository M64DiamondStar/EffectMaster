package me.m64diamondstar.effectmaster.editor.wand

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Colors.Color
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class Wand(val id: String, val displayName: String) {

    companion object {

        fun isWand(item: ItemStack): Boolean {
            return item.hasItemMeta() && item.itemMeta!!.persistentDataContainer.has(NamespacedKey(EffectMaster.plugin(), "wand_id"))
        }

        fun getWand(item: ItemStack?): Wand? {
            if(item == null) return null
            if(!isWand(item)) return null
            val itemId = item.itemMeta!!.persistentDataContainer.get(NamespacedKey(EffectMaster.plugin(), "wand_id"),
                PersistentDataType.STRING)
            return WandRegistry.getRegisteredWands().firstOrNull { it.id == itemId }
        }

        fun getWand(id: String): Wand? {
            return WandRegistry.getRegisteredWands().firstOrNull { it.id == id }
        }

    }

    /**
     * A list of wand modes. This is also the order in which the modes switch.
     */
    abstract fun getModes(): List<WandMode>

    /**
     * The required permission in order to be able to use this wand.
     */
    abstract fun getPermission(): String

    /**
     * Give the wand to a player
     * @return whether the action was successful
     */
    fun givePlayer(player: Player): Boolean{
        if(getModes().isEmpty()) return false

        player.inventory.addItem(getWand())
        return true
    }

    /**
     * The item stack of the wand.
     */
    fun getWand(): ItemStack? {
        if(getModes().isEmpty()) return null

        val item = ItemStack(getModes().first().getMaterial())
        val meta = item.itemMeta ?: return null

        meta.setEnchantmentGlintOverride(true)
        meta.setDisplayName(displayName)
        meta.lore = getModes().first().getDescription().map { Colors.format("${Color.DEFAULT}$it") }
        meta.persistentDataContainer.set(
            NamespacedKey(EffectMaster.plugin(), "wand_id"),
            PersistentDataType.STRING,
            id
        )
        meta.persistentDataContainer.set(
            NamespacedKey(EffectMaster.plugin(), "wand_mode"),
            PersistentDataType.STRING,
            getModes().first().getId()
        )

        item.itemMeta = meta
        return item
    }

    fun getMode(item: ItemStack?): WandMode? {
        if(item == null) return null
        val modeId = item.itemMeta!!.persistentDataContainer.get(NamespacedKey(EffectMaster.plugin(), "wand_mode"),
            PersistentDataType.STRING)

        return this.getModes().first { it.getId() == modeId } // Get the first mode where the ID is the same as the ID saved on the item
    }

    /**
     * @return the next mode
     */
    fun getNextMode(item: ItemStack?): WandMode? {
        val currentMode = getMode(item)
        val currentIndex = this.getModes().indexOf(currentMode)
        val nextIndex = if(currentIndex + 1 >= this.getModes().size) 0 else currentIndex + 1
        return this.getModes()[nextIndex]
    }

    /**
     * Changes the current wand to the next mode.
     */
    fun nextMode(item: ItemStack): ItemStack? {
        val nextMode = getNextMode(item) ?: return null
        item.type = nextMode.getMaterial()
        val meta = item.itemMeta ?: return null

        meta.lore = nextMode.getDescription()
            .map {
                listOf(
                    Colors.format("#ffffff⦿ &o${it.action}"),
                    Colors.format("   ${Color.DEFAULT}${it.description}")
                )
            }
            .reduceIndexed { index, acc, list ->
                if (index == 0) acc + list else acc + listOf("") + list
            }

        meta.persistentDataContainer.set(
            NamespacedKey(EffectMaster.plugin(), "wand_mode"),
            PersistentDataType.STRING,
            nextMode.getId()
        )

        item.itemMeta = meta
        return item
    }

}