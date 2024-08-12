package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object TypeData {

    fun setIdentifier(itemStack: ItemStack, identifier: String): ItemStack {
        val meta = itemStack.itemMeta!!
        meta.persistentDataContainer.set(
            NamespacedKey(EffectMaster.plugin(), "identifier"),
            PersistentDataType.STRING,
            identifier
        )
        itemStack.itemMeta = meta
        return itemStack
    }

    fun getIdentifier(itemStack: ItemStack): String? =
        itemStack.itemMeta!!.persistentDataContainer.get(NamespacedKey(EffectMaster.plugin(), "identifier"), PersistentDataType.STRING)


}