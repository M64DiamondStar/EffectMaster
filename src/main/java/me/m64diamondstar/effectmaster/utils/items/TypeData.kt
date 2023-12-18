package me.m64diamondstar.effectmaster.utils.items

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object TypeData {

    fun setType(itemStack: ItemStack, effectType: Effect.Type): ItemStack {
        val meta = itemStack.itemMeta!!
        meta.persistentDataContainer.set(
            NamespacedKey(EffectMaster.plugin(), "type"),
            PersistentDataType.STRING,
            effectType.toString()
        )
        itemStack.itemMeta = meta
        return itemStack
    }

    fun getType(itemStack: ItemStack): Effect.Type? {
        val effectType = itemStack.itemMeta!!.persistentDataContainer.get(NamespacedKey(EffectMaster.plugin(), "type"), PersistentDataType.STRING) ?: return null
        return try {
            Effect.Type.valueOf(effectType)
        }catch (ex: IllegalArgumentException){
            null
        }
    }

}