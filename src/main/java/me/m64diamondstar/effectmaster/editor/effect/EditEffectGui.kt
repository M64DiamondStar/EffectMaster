package me.m64diamondstar.effectmaster.editor.effect

import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.shows.utils.ParameterType
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class EditEffectGui(private val player: Player, private val id: Int, show: Show): Gui(player = player) {

    private val showCategory: String = show.getCategory()
    private val showName: String = show.getName()

    override fun setDisplayName(): String {
        return "Editing effect for $showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {

        if((event.slot in 10..16 || event.slot in 19..25)){
            val item = event.currentItem!!
            val meta = item.itemMeta!!

            try{
                // ParameterType.valueOf(meta.displayName.split(": ")[1].uppercase())
                val description = ParameterType.valueOf(meta.displayName.split(": ")[1].uppercase()).getInfo()
                description.forEach { player.sendMessage(Colors.format(Colors.Color.BACKGROUND.toString() + it)) }

                player.sendMessage(" ")

                val example = ParameterType.valueOf(meta.displayName.split(": ")[1].uppercase()).getExample()
                player.sendMessage(Colors.format(Colors.Color.STANDARD.toString() + "Example: $example"))

                player.sendMessage(Colors.format(Colors.Color.STANDARD.toString() + ""))

                player.sendMessage(Colors.format(Colors.Color.ERROR.toString() + "To cancel this edit, please type &ocancel"))

                player.closeInventory()

                EditingPlayers.add(player, Show(showCategory, showName), id, meta.displayName.split(": ")[1])
            }catch (e: IllegalArgumentException){
                player.closeInventory()
                player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "This parameter type does not exist."))
            }
        }

        if(event.slot == 38){
            val show = Show(showCategory, showName)
            val editShowGui = EditShowGui(player, show)
            editShowGui.open()
        }

        if(event.slot == 42){ // 'Delete' is clicked
            if(event.currentItem!!.containsEnchantment(Enchantment.DURABILITY)){ // Already clicked once.
                val show = Show(showCategory, showName)
                show.deleteEffect(id)

                val editShowGui = EditShowGui(player, show)
                editShowGui.open()
            }else{ // Add glow and add lore to confirm deletion
                event.currentItem!!.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                val meta = event.currentItem!!.itemMeta!!
                meta.lore = listOf(Colors.format(Colors.Color.ERROR.toString() + "Please click again to confirm deletion."))
                event.currentItem!!.itemMeta = meta
            }
        }

        if(event.slot == 49){ // Play only this effect
            val show = Show(showCategory, showName)
            show.playOnly(id)

            player.closeInventory()
            val clickableComponent = TextComponent(TextComponent("Click here to re-open the edit gui."))
            clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
            clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/em editor ${show.getCategory()} ${show.getName()} $id")
            clickableComponent.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("Click me to re-open the edit gui.").create())
            player.spigot().sendMessage(clickableComponent)
        }

    }

    override fun setInventoryItems() {
        for(i in 0..35) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 36..53) inventory.setItem(i, GuiItems.getGrayPane())
        for(i in 10..16) inventory.setItem(i, null)
        for(i in 19..25) inventory.setItem(i, null)

        inventory.setItem(38, GuiItems.getBack())
        inventory.setItem(42, GuiItems.getDelete())
        inventory.setItem(49, GuiItems.getPlay())

        updatePreview()
    }

    // Sets display item of current effect in 40th slot.
    private fun updatePreview() {
        val show = Show(showCategory, showName)
        val effect = show.getEffect(id) ?: return

        val preview = ItemStack(effect.getType().getDisplayMaterial())
        val previewMeta = preview.itemMeta!!
        val lore = ArrayList<String>()

        previewMeta.setDisplayName(Colors.format("#dcb5ff&l${effect.getType().toString().lowercase()
            .replace("_", " ").replaceFirstChar(Char::titlecase)}"))
        lore.add(" ")
        effect.getSection().getKeys(false).forEach { section ->
            lore.add(Colors.format("#a8a8a8$section: &r#e0e0e0&o${effect.getSection().get(section).toString()}"))
        }

        previewMeta.lore = lore

        preview.itemMeta = previewMeta

        inventory.setItem(40, preview)


        for(setting in effect.getDefaults()){
            if(!setting.first.equals("Type", ignoreCase = true)) {
                val item = ItemStack(Material.MAP)
                val meta = item.itemMeta!!

                meta.setDisplayName(Colors.format("#dcb5ffEdit: ${setting.first}"))
                meta.lore = listOf(
                    Colors.format(
                        Colors.Color.BACKGROUND.toString() + "Currently set to: ${
                            effect.getSection().get(setting.first)
                        }"
                    ),
                    " ",
                    Colors.format(Colors.Color.BACKGROUND.toString() + "&oClick to edit")
                )

                item.itemMeta = meta
                inventory.addItem(item)
            }
        }

    }

}