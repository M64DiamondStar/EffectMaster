package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.sessions.EffectSorting
import me.m64diamondstar.effectmaster.editor.sessions.UserPreferences
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.ktx.plainText
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import me.m64diamondstar.effectmaster.utils.items.TypeData
import net.kyori.adventure.audience.Audience
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * @param page the page (of effects) to open the GUI with, starts at page 0!
 */
class AllEffectsGui(
    private val player: Player,
    private val effectShow: EffectShow,
    private val page: Int
) : Gui(player = player) {

    private val showCategory = effectShow.getCategory()
    private val showName = effectShow.getName()
    private val userPreferences = UserPreferences.get(player)

    override fun setDisplayName() = "View All Effects..."
    override fun setSize() = 54

    override fun handleInventory(event: InventoryClickEvent) {

        when (event.slot) {
            in 0..44 -> { // Edit effect
                if (event.currentItem != null && !TypeData.isInvalidEffect(event.currentItem!!)) {
                    val id = event.currentItem!!.itemMeta!!.customName()?.plainText()?.split(": ")[1]?.toInt() ?: return
                    EditEffectGui(player, id, effectShow, 0).open()
                }
            }
            47 -> { // Sorting button
                val current = userPreferences.getPreference(
                    UserPreferences.Defaults.EFFECT_SORTING,
                    EffectSorting.SMALLEST_ID
                )

                val values = EffectSorting.entries
                val nextIndex = if (event.isLeftClick) {
                    (current.ordinal + 1) % values.size
                } else {
                    (current.ordinal - 1 + values.size) % values.size
                }
                val next = values[nextIndex]

                userPreferences.setPreference(UserPreferences.Defaults.EFFECT_SORTING, next)

                val newGui = AllEffectsGui(player, effectShow, page)
                newGui.open()
            }
            48 -> if (event.currentItem?.type == Material.ARROW && page > 0) {
                AllEffectsGui(player, effectShow, page - 1).open()
            }
            49 -> EditShowGui(player, effectShow).open()
            50 -> if (event.currentItem?.type == Material.ARROW) {
                AllEffectsGui(player, effectShow, page + 1).open()
            }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        (player as Audience).sendMessage(
            emComponent(
                "<click:run_command:'/em editor $showCategory $showName all'>" +
                        "<default>Click here to re-open the all-effects editor."
            )
        )
    }

    override fun setInventoryItems() {
        for (i in 45..53) inventory.setItem(i, GuiItems.getBlackPane())

        inventory.setItem(49, GuiItems.getBack())

        val sorted = getSortedEffects(effectShow)

        if (page > 0) inventory.setItem(48, GuiItems.getScrollBack())
        if (sorted.size > (page + 1) * 45) inventory.setItem(50, GuiItems.getScrollFurther())

        // Sorting button in slot 47
        val sorting = userPreferences.getPreference(
            UserPreferences.Defaults.EFFECT_SORTING,
            EffectSorting.SMALLEST_ID
        )
        inventory.setItem(
            47,
            GuiItems.getSorting(EffectSorting.entries.map { it.toString() }, sorting.ordinal)
        )

        // Render effects for current page
        val start = page * 45
        val end = minOf(start + 45, sorted.size)

        for (i in start until end) {
            val (id, effect) = sorted[i]
            if (effect == null) {
                inventory.addItem(GuiItems.getInvalidEffect())
                continue
            }

            inventory.addItem(GuiItems.getEffectItem(effectShow, id))
        }
    }

    private fun getSortedEffects(effectShow: EffectShow): List<Pair<Int, Effect?>> {
        val effects = effectShow.getAllEffects()
        return when (
            userPreferences.getPreference(
                UserPreferences.Defaults.EFFECT_SORTING,
                EffectSorting.SMALLEST_ID
            )
        ) {
            EffectSorting.SMALLEST_ID -> effects.toSortedMap().toList()
            EffectSorting.LARGEST_ID -> effects.toSortedMap(compareByDescending { it }).toList()
            EffectSorting.EFFECT_TYPE ->
                effects.entries.sortedBy { it.value?.getIdentifier() ?: "" }.map { it.toPair() }
            EffectSorting.DELAY ->
                effects.entries.sortedBy { effectShow.getDelay(it.key) }.map { it.toPair() }
        }
    }
}
