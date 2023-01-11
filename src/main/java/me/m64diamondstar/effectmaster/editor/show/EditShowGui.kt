package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiFiller
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class EditShowGui(private val player: Player, show: Show): Gui(player = player) {

    private val showCategory: String = show.getCategory()
    private val showName: String = show.getName()

    override fun setDisplayName(): String {

        return "Editing showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {

        if(event.slot == 40){ // 'Play' is clicked
            val show = Show(showCategory, showName)
            Bukkit.getScheduler().runTask(EffectMaster.plugin, Runnable {
                show.play()
            })
            player.closeInventory()
            val clickableComponent = TextComponent(TextComponent("Click here to re-open the edit gui."))
            clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
            clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/em editor ${show.getCategory()} ${show.getName()}")
            clickableComponent.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("Click me to re-open the edit gui.").create())
            player.spigot().sendMessage(clickableComponent)
        }

        if(event.slot == 21){ // 'Scroll Back' is clicked

            if(event.inventory.getItem(9) == null)
                return

            val minID = if(inventory.getItem(9)!!.itemMeta!!.displayName.split(" ").last().toInt() == 1) // Gets the ID of the current item
                return
            else
                inventory.getItem(9)!!.itemMeta!!.displayName.split(" ").last().toInt() - 1
            val maxID = minID + 8

            val show = Show(showCategory, showName)
            for(i in 9..17){ // Clear all slots
                event.inventory.setItem(i, ItemStack(Material.AIR))
            }

            // Make sure the red and green panes disappear
            event.inventory.setItem(8, GuiFiller.getBlackPane())
            event.inventory.setItem(26, GuiFiller.getBlackPane())

            val effects = show.getAllEffects()
            var i = 1
            effects.forEach {

                if(it.getID() !in minID..maxID)
                    return@forEach
                if(i >= 10)
                    return@forEach

                val item = ItemStack(it.getType().getDisplayMaterial())
                val meta = item.itemMeta!!
                val lore = ArrayList<String>()

                meta.setDisplayName(Colors.format("#dcb5ff&l${it.getType().toString().lowercase().replace("_", " ").replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: ${it.getID()}"))
                lore.add(" ")
                it.getSection().getKeys(false).forEach { section ->
                    lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${it.getSection().get(section).toString()}"))
                }
                meta.lore = lore

                item.itemMeta = meta

                event.inventory.setItem(8 + i, item)
                i++
            }

            if(minID == 1){
                event.inventory.setItem(0, GuiFiller.getGreenPane())
                event.inventory.setItem(18, GuiFiller.getGreenPane())
            }
        }

        if(event.slot == 23){ // 'Scroll Further' is clicked
            if(event.inventory.getItem(17) == null)
                return

            val show = Show(showCategory, showName)

            val minID = if(inventory.getItem(17)!!.itemMeta!!.displayName.split(" ").last().toInt() == show.getMaxId()) // Gets the ID of the current item
                return
            else
                inventory.getItem(9)!!.itemMeta!!.displayName.split(" ").last().toInt() + 1
            val maxID = minID + 8

            for(i in 9..17){ // Clear all slots
                inventory.setItem(i, ItemStack(Material.AIR))
            }

            // Make sure the red and green panes disappear
            event.inventory.setItem(0, GuiFiller.getBlackPane())
            event.inventory.setItem(18, GuiFiller.getBlackPane())

            val effects = show.getAllEffects()
            var i = 1
            effects.forEach { //Add all effects

                if(it.getID() !in minID..maxID)
                    return@forEach
                if(i >= 10)
                    return@forEach

                val item = ItemStack(it.getType().getDisplayMaterial())
                val meta = item.itemMeta!!
                val lore = ArrayList<String>()

                meta.setDisplayName(Colors.format("#dcb5ff&l${it.getType().toString().lowercase().replace("_", " ").replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: ${it.getID()}"))
                lore.add(" ")
                it.getSection().getKeys(false).forEach { section ->
                    lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${it.getSection().get(section).toString()}"))
                }
                meta.lore = lore

                item.itemMeta = meta

                inventory.setItem(8 + i, item)
                i++
            }

            if(maxID == show.getMaxId()){
                event.inventory.setItem(8, GuiFiller.getRedPane())
                event.inventory.setItem(26, GuiFiller.getRedPane())
            }
        }

    }

    override fun setInventoryItems() {

        val show = Show(showCategory, showName)

        // Add glass panes
        for(i in 0..8) inventory.setItem(i, GuiFiller.getBlackPane())
        for(i in 18..26) inventory.setItem(i, GuiFiller.getBlackPane())
        for(i in 27..53) inventory.setItem(i, GuiFiller.getGrayPane())

        // Add basic items
        inventory.setItem(21, GuiFiller.getScrollBack())
        inventory.setItem(23, GuiFiller.getScrollFurther())
        inventory.setItem(40, GuiFiller.getPlay())

        // Add green panes to show first effect
        inventory.setItem(0, GuiFiller.getGreenPane())
        inventory.setItem(18, GuiFiller.getGreenPane())

        val effects = show.getAllEffects()
        var i = 1
        effects.forEach {
            if(i >= 10)
                return@forEach

            val item = ItemStack(it.getType().getDisplayMaterial())
            val meta = item.itemMeta!!
            val lore = ArrayList<String>()

            meta.setDisplayName(Colors.format("#dcb5ff&l${it.getType().toString().lowercase().replace("_", " ").replaceFirstChar(Char::titlecase)} &r#8f8f8f&oID: ${it.getID()}"))
            lore.add(" ")
            it.getSection().getKeys(false).forEach { section ->
                lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${it.getSection().get(section).toString()}"))
            }
            meta.lore = lore



            item.itemMeta = meta

            inventory.setItem(8 + i, item)
            i++
        }


    }
}