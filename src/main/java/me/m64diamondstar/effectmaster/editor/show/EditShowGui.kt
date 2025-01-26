package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.editor.effect.CreateEffectGui
import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import me.m64diamondstar.effectmaster.utils.items.TypeData
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class EditShowGui(private val player: Player, effectShow: EffectShow): Gui(player = player) {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()

    override fun setDisplayName(): String {
        return "Editing $showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {

        if(event.slot in 9..17 && event.currentItem != null && !TypeData.isInvalidEffect(event.currentItem!!)){ // Start editing one of the effects
            val id = event.currentItem!!.itemMeta!!.displayName.split(": ")[1].toInt()
            val effectShow = EffectShow(showCategory, showName)
            val editEffectGui = EditEffectGui(player, id, effectShow, 0)
            editEffectGui.open()
        }

        if(event.slot == 19){ // 'View All Effects' is clicked
            val effectShow = EffectShow(showCategory, showName)
            val allEffectsGui = AllEffectsGui(player, effectShow, 0)
            allEffectsGui.open()
        }

        if(event.slot == 38){ // 'New Effect' is clicked
            val effectShow = EffectShow(showCategory, showName)
            val createEffectGui = CreateEffectGui(event.whoClicked as Player, effectShow, 0)
            createEffectGui.open()
        }

        if(event.slot == 40){ // 'Play' is clicked
            val effectShow = EffectShow(showCategory, showName)
            EffectMaster.getFoliaLib().scheduler.runNextTick { task ->
                effectShow.play(null)
            }
            player.closeInventory()
            val clickableComponent = TextComponent(TextComponent("Click here to re-open the edit gui."))
            clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
            clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/em editor ${effectShow.getCategory()} ${effectShow.getName()}")
            clickableComponent.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("Click me to re-open the edit gui.").create())
            player.spigot().sendMessage(clickableComponent)
        }

        if(event.slot == 42){
            val showSettingsGui = ShowSettingsGui(event.whoClicked as Player, EffectShow(showCategory, showName))
            showSettingsGui.open()
        }

        if(event.slot == 21){ // 'Scroll Back' is clicked

            if(event.inventory.getItem(9) == null)
                return

            val minID = if(inventory.getItem(9)!!.itemMeta!!.displayName.split(" ").last().toInt() == 1) // Gets the ID of the current item
                return
            else
                inventory.getItem(9)!!.itemMeta!!.displayName.split(" ").last().toInt() - 1
            val maxID = minID + 8

            val effectShow = EffectShow(showCategory, showName)
            for(i in 9..17){ // Clear all slots
                event.inventory.setItem(i, ItemStack(Material.AIR))
            }

            // Make sure the red and green panes disappear
            event.inventory.setItem(8, GuiItems.getBlackPane())
            event.inventory.setItem(26, GuiItems.getBlackPane())

            val effects = effectShow.getAllEffects()
            var i = 1
            effects.forEach { id, effect ->
                if(effect == null){
                    inventory.setItem(8 + i, GuiItems.getInvalidEffect())
                    i++
                    return@forEach
                }
                if(id !in minID..maxID)
                    return@forEach
                if(i >= 10)
                    return@forEach

                val item = ItemStack(effect.getDisplayMaterial())
                val meta = item.itemMeta!!
                val lore = ArrayList<String>()

                meta.setDisplayName(Colors.format("#dcb5ff&l${effect.getIdentifier().toString().lowercase().replace("_", " ").replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: $id"))
                lore.add(" ")
                effect.getSection(effectShow, id).getKeys(false).forEach { section ->
                    lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${effect.getSection(effectShow, id).get(section).toString()}"))
                }
                meta.lore = lore
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

                item.itemMeta = meta

                event.inventory.setItem(8 + i, item)
                i++
            }

            if(minID == 1){
                event.inventory.setItem(0, GuiItems.getGreenPane())
                event.inventory.setItem(18, GuiItems.getGreenPane())
            }
        }

        if(event.slot == 23){ // 'Scroll Further' is clicked
            if(event.inventory.getItem(17) == null)
                return

            val effectShow = EffectShow(showCategory, showName)

            val minID = if(inventory.getItem(17)!!.itemMeta!!.displayName.split(" ").last().toInt() == effectShow.getMaxId()) // Gets the ID of the current item
                return
            else
                inventory.getItem(9)!!.itemMeta!!.displayName.split(" ").last().toInt() + 1
            val maxID = minID + 8

            for(i in 9..17){ // Clear all slots
                inventory.setItem(i, ItemStack(Material.AIR))
            }

            // Make sure the red and green panes disappear
            event.inventory.setItem(0, GuiItems.getBlackPane())
            event.inventory.setItem(18, GuiItems.getBlackPane())

            val effects = effectShow.getAllEffects()
            var i = 1
            effects.forEach { id, effect -> //Add all effects
                if(effect == null){
                    inventory.setItem(8 + i, GuiItems.getInvalidEffect())
                    i++
                    return@forEach
                }
                if(id !in minID..maxID)
                    return@forEach
                if(i >= 10)
                    return@forEach

                val item = ItemStack(effect.getDisplayMaterial())
                val meta = item.itemMeta!!
                val lore = ArrayList<String>()

                meta.setDisplayName(Colors.format("#dcb5ff&l${effect.getIdentifier().toString().lowercase().replace("_", " ").replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: $id"))
                lore.add(" ")
                effect.getSection(effectShow, id).getKeys(false).forEach { section ->
                    lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${effect.getSection(effectShow, id)[section].toString()}"))
                }
                meta.lore = lore
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

                item.itemMeta = meta

                inventory.setItem(8 + i, item)
                i++
            }

            if(maxID == effectShow.getMaxId()){
                event.inventory.setItem(8, GuiItems.getRedPane())
                event.inventory.setItem(26, GuiItems.getRedPane())
            }
        }

    }

    override fun setInventoryItems() {

        val effectShow = EffectShow(showCategory, showName)

        // Add glass panes
        for(i in 0..8) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 18..26) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 27..53) inventory.setItem(i, GuiItems.getGrayPane())

        // Add basic items
        inventory.setItem(19, GuiItems.getViewAll())
        inventory.setItem(21, GuiItems.getScrollBack())
        inventory.setItem(23, GuiItems.getScrollFurther())

        inventory.setItem(38, GuiItems.getCreateEffect())
        inventory.setItem(40, GuiItems.getPlay())
        inventory.setItem(42, GuiItems.getSettings())

        // Add green panes to show first effect
        inventory.setItem(0, GuiItems.getGreenPane())
        inventory.setItem(18, GuiItems.getGreenPane())

        val effects = effectShow.getAllEffects()
        var i = 1
        effects.forEach { id, effect ->
            if(effect == null){
                inventory.setItem(8 + i, GuiItems.getInvalidEffect())
                i++
                return@forEach
            }
            if(i >= 10)
                return@forEach

            val item = ItemStack(effect.getDisplayMaterial())
            val meta = item.itemMeta!!
            val lore = ArrayList<String>()

            meta.setDisplayName(Colors.format("#dcb5ff&l${effect.getIdentifier().toString().lowercase().replace("_", " ").replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: $id"))
            lore.add(" ")
            effect.getSection(effectShow, id).getKeys(false).forEach { parameter ->
                var value = effect.getSection(effectShow, id).get(parameter).toString()
                var sectionString = "${Colors.Color.BACKGROUND}$parameter: ${Colors.Color.DEFAULT}"

                if(value.length + parameter.length > 60){
                    value = value.substring(0, 57 - parameter.length) + "..."
                }

                lore.add(Colors.format("&r#e0e0e0&o$sectionString") + value)
            }
            lore.add(" ")
            lore.add(Colors.format(Colors.Color.SUCCESS.toString() + "Click to edit!"))
            meta.lore = lore
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

            item.itemMeta = meta

            inventory.setItem(8 + i, item)
            i++
        }


    }
}