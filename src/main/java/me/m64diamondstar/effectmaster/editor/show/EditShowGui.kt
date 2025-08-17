package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.editor.effect.CreateEffectGui
import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.sessions.EffectSorting
import me.m64diamondstar.effectmaster.editor.sessions.UserPreferences
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import me.m64diamondstar.effectmaster.utils.items.TypeData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class EditShowGui(private val player: Player, private val effectShow: EffectShow): Gui(player = player) {

    private val userPreferences = UserPreferences.get(player)
    private val showCategory = effectShow.getCategory()
    private val showName = effectShow.getName()

    private var pageIndex = 0

    override fun setDisplayName(): String {
        return "Editing $showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {
        when (event.slot) {
            in 9..17 -> event.currentItem?.takeIf { !TypeData.isInvalidEffect(it) }?.let { item ->
                val id = item.itemMeta!!.displayName.split(": ")[1].toInt()
                EditEffectGui(player, id, effectShow, 0).open()
            }
            19 -> AllEffectsGui(player, effectShow, 0).open()
            38 -> CreateEffectGui(player, effectShow, 0).open()
            40 -> {
                EffectMaster.getFoliaLib().scheduler.runNextTick { effectShow.play(null) }
                player.closeInventory()
            }
            42 -> ShowSettingsGui(player, effectShow).open()
            22 -> handleSortingClick(event)
            21 -> if (pageIndex > 0) { pageIndex--; renderPage() }
            23 -> if ((pageIndex + 1) * 9 < getSortedEffects().size) { pageIndex++; renderPage() }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize(
            "<click:run_command:'/em editor $showCategory $showName'>" +
                    "<${Colors.Color.BACKGROUND}>Click here to re-open the show editor."
        ))
    }

    override fun setInventoryItems() {

        // Add glass panes
        for(i in 0..8) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 18..26) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 27..53) inventory.setItem(i, GuiItems.getGrayPane())

        // Add basic items
        inventory.setItem(19, GuiItems.getViewAll())
        inventory.setItem(21, GuiItems.getScrollBack())

        val sorting = userPreferences.getPreference(UserPreferences.Defaults.EFFECT_SORTING, EffectSorting.SMALLEST_ID)
        userPreferences.setPreference(
            UserPreferences.Defaults.EFFECT_SORTING,
            sorting
        )
        inventory.setItem(22, GuiItems.getSorting(EffectSorting.entries.map { it.toString() },
            sorting.ordinal)
        )

        inventory.setItem(23, GuiItems.getScrollFurther())

        inventory.setItem(38, GuiItems.getCreateEffect())
        inventory.setItem(40, GuiItems.getPlay())
        inventory.setItem(42, GuiItems.getSettings())

        // Render the page (based on the selected sorting)
        renderPage()
    }

    /**
     * Gets all the effects sorted by the selected sorting method
     */
    private fun getSortedEffects(): List<Pair<Int, Effect?>> {
        val effects = effectShow.getAllEffects()
        return when (userPreferences.getPreference(UserPreferences.Defaults.EFFECT_SORTING, EffectSorting.SMALLEST_ID)) {
            EffectSorting.SMALLEST_ID -> effects.toSortedMap().toList()
            EffectSorting.LARGEST_ID -> effects.toSortedMap(compareByDescending { it }).toList()
            EffectSorting.EFFECT_TYPE -> effects.entries.sortedBy { it.value?.getIdentifier() ?: "" }.map { it.toPair() }
            EffectSorting.DELAY -> effects.entries.sortedBy { effectShow.getDelay(it.key) }.map { it.toPair() }
        }
    }


    /**
     * Renders the effects row
     */
    private fun renderPage() {
        val sorted = getSortedEffects()
        val start = pageIndex * 9
        val end = (start + 9).coerceAtMost(sorted.size)

        // Clear slots
        (9..17).forEach { inventory.setItem(it, ItemStack(Material.AIR)) }

        // Fill page
        sorted.subList(start, end).forEachIndexed { index, (id, _) ->
            inventory.setItem(9 + index, GuiItems.getEffectItem(effectShow, id))
        }

        // Scroll indicators
        val atStart = pageIndex == 0
        val atEnd = end == sorted.size
        inventory.setItem(0, if (atStart) GuiItems.getGreenPane() else GuiItems.getBlackPane())
        inventory.setItem(18, if (atStart) GuiItems.getGreenPane() else GuiItems.getBlackPane())
        inventory.setItem(8, if (atEnd) GuiItems.getRedPane() else GuiItems.getBlackPane())
        inventory.setItem(26, if (atEnd) GuiItems.getRedPane() else GuiItems.getBlackPane())
    }

    /**
     * Handles everything that needs to happen when the sort button is clicked
     */
    private fun handleSortingClick(event: InventoryClickEvent) {
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

        // Update item
        inventory.setItem(22, GuiItems.getSorting(
            values.map { it.toString() },
            next.ordinal
        ))

        pageIndex = 0
        renderPage()
    }

}