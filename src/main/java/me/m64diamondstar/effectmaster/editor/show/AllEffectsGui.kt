package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import me.m64diamondstar.effectmaster.utils.items.TypeData
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * @param page the page (of effects) to open the GUI with, starts at page 0!
 */
class AllEffectsGui(private val player: Player, effectShow: EffectShow, private val page: Int): Gui(player = player)  {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()

    override fun setDisplayName(): String {
        return "View All Effects..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {

        // Edit an effect
        if(event.slot in 0..44 && event.currentItem != null && !TypeData.isInvalidEffect(event.currentItem!!)){ // Start editing one of the effects
            val id = event.currentItem!!.itemMeta!!.displayName.split(": ")[1].toInt()
            val effectShow = EffectShow(showCategory, showName, null)
            val editEffectGui = EditEffectGui(player, id, effectShow)
            editEffectGui.open()
        }

        // Go back to main menu
        if(event.slot == 49){
            val effectShow = EffectShow(showCategory, showName, null)
            val editShowGui = EditShowGui(player, effectShow)
            editShowGui.open()
        }

        // Scroll through the pages
        if(event.currentItem != null && event.currentItem!!.type == Material.ARROW){

            // Scroll back
            if(event.slot == 48){
                val effectShow = EffectShow(showCategory, showName, null)
                val allEffectsGui = AllEffectsGui(player, effectShow, page - 1)
                allEffectsGui.open()
            }

            // Scroll further
            if(event.slot == 50){
                val effectShow = EffectShow(showCategory, showName, null)
                val allEffectsGui = AllEffectsGui(player, effectShow, page + 1)
                allEffectsGui.open()
            }
        }
    }

    override fun setInventoryItems() {
        for(i in 45..53){
            inventory.setItem(i, GuiItems.getBlackPane())
        }

        inventory.setItem(49, GuiItems.getBack())

        val effectShow = EffectShow(showCategory, showName, null)
        val effects = effectShow.getAllEffects()

        if(page > 0)
            inventory.setItem(48, GuiItems.getScrollBack())

        if(effects.size > (page + 1) * 45)
            inventory.setItem(50, GuiItems.getScrollFurther())

        /*
        Starts at page 0!
        Checks if there are any effects on the selected page
         */
        if(effects.size > page * 45){

            for(i in page * 45 until effects.size){

                val id = i + 1
                val effect = effects[id]
                if(effect == null){
                    inventory.addItem(GuiItems.getInvalidEffect())
                    continue
                }

                val item = ItemStack(effect.getDisplayMaterial())
                val meta = item.itemMeta!!
                val lore = ArrayList<String>()

                meta.setDisplayName(Colors.format("#dcb5ff&l${effect.getIdentifier().toString().lowercase().replace("_", " ")
                    .replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: $id"))
                lore.add(" ")
                effect.getSection(effectShow, id).getKeys(false).forEach { section ->
                    lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${effect.getSection(effectShow, id).get(section).toString()}"))
                }
                meta.lore = lore
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

                item.itemMeta = meta

                inventory.addItem(item)

            }
        }
    }
}