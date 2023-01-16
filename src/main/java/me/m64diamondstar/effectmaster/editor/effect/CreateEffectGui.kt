package me.m64diamondstar.effectmaster.editor.effect

import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CreateEffectGui(private val player: Player, show: Show): Gui(player = player) {

    private val showCategory: String = show.getCategory()
    private val showName: String = show.getName()

    override fun setDisplayName(): String {
        return "Creating effect for $showName..."
    }

    override fun setSize(): Int {
        return 36
    }

    override fun handleInventory(event: InventoryClickEvent) {
        if(event.currentItem == null) return

        Effect.Type.values().forEach {
            if(event.currentItem!!.itemMeta!!.lore?.last()?.split(": ")!![1] == it.toString()){

                val show = Show(showCategory, showName)
                val id = show.getMaxId() + 1
                val effect = it.getTypeClass(show, id)
                show.setDefaults(id, effect.getDefaults())

                val editShowGui = EditShowGui(player, Show(showCategory, showName))
                editShowGui.open()

            }
        }
    }

    override fun setInventoryItems() {
        for(i in 0..8) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 27..35) inventory.setItem(i, GuiItems.getBlackPane())
        val item = ItemStack(Material.STONE)
        val meta = item.itemMeta!!
        Effect.Type.values().forEach {
            item.type = it.getDisplayMaterial()
            meta.setDisplayName(Colors.format("#dcb5ff&l${it.toString().lowercase().replace("_", " ")
                .replaceFirstChar(Char::titlecase)}"))
            meta.lore = listOf(
                Colors.format(Colors.Color.BACKGROUND.toString() + "Click to choose this effect."),
                Colors.format(Colors.Color.BACKGROUND.toString() + "Enum Type: $it")
            )
            item.itemMeta = meta
            inventory.addItem(item)
        }
    }
}