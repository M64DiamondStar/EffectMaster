package me.m64diamondstar.effectmaster.editor.effect

import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix.PrefixType
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class EditEffectGui(private val player: Player, private val id: Int, effectShow: EffectShow, private val page: Int): Gui(player = player) {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()

    override fun setDisplayName(): String {
        return "Editing effect for $showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {

        // Check if player clicks in upper inventory
        if(event.clickedInventory!!.type != InventoryType.CHEST) return

        if(event.slot in 10..16 || event.slot in 19..25){
            val item = event.currentItem!!
            val meta = item.itemMeta!!
            val effectShow = EffectShow(showCategory, showName)
            val effect = effectShow.getEffect(id)!! // Player can't be editing an effect which is null

            try{
                val parameter = effect.getDefaults().find { meta.displayName.split(": ")[1] == it.name } ?: return
                val description = parameter.description

                player.sendMessage(Colors.format(PrefixType.DEFAULT.toString() + "Parameter description:"))
                player.sendMessage(Colors.format(Colors.Color.BACKGROUND.toString() + description))
                (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize("<br>" +
                        "<${Colors.Color.DEFAULT}>" +
                        "<click:copy_to_clipboard:'${effect.getSection(effectShow, id).getString(meta.displayName.split(": ")[1])}'>" +
                        "<hover:show_text:'${effect.getSection(effectShow, id).getString(meta.displayName.split(": ")[1])}'>" +
                        "Click here to copy the current value.<br>"))

                (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize(
                        "<${Colors.Color.ERROR}>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:Or click this text to cancel.>" +
                        "To cancel this edit, please type <italic>cancel."))

                player.closeInventory()

                EditingPlayers.add(player, EffectShow(showCategory, showName), id, parameter)
            }catch (_: IllegalArgumentException){
                player.closeInventory()
                player.sendMessage(Colors.format(PrefixType.ERROR.toString() + "This parameter type does not exist."))
            }
        }

        if(event.slot == 30 && event.currentItem!!.type == Material.ARROW){ // 'Edit' is clicked
            val editEffectGui = EditEffectGui(player, id, EffectShow(showCategory, showName), page - 1)
            editEffectGui.open()
        }

        if(event.slot == 32 && event.currentItem!!.type == Material.ARROW){ // 'Edit' is clicked
            val editEffectGui = EditEffectGui(player, id, EffectShow(showCategory, showName), page + 1)
            editEffectGui.open()
        }

        if(event.slot == 36){ // 'Duplicate' is clicked
            val effectShow = EffectShow(showCategory, showName)
            val effect = effectShow.getEffect(id)
            val newId = effectShow.getMaxId() + 1

            val list = ArrayList<ParameterLike>()
            if (effect != null) {
                for (key in effect.getSection(effectShow, id).getKeys(false)) {
                    list.add(Parameter(key!!, effect.getSection(effectShow, id).get(key)!!, "", {it}, { true }))
                }
            }
            effectShow.setDefaults(newId, list)

            val editShowGui = EditShowGui(player, effectShow)
            editShowGui.open()
        }

        if(event.slot == 38){ // 'Back' is clicked
            val effectShow = EffectShow(showCategory, showName)
            val editShowGui = EditShowGui(player, effectShow)
            editShowGui.open()
        }

        if(event.slot == 42){ // 'Delete' is clicked
            if(event.currentItem!!.itemMeta!!.hasEnchantmentGlintOverride()){ // Already clicked once.
                val effectShow = EffectShow(showCategory, showName)
                effectShow.deleteEffect(id)

                val editShowGui = EditShowGui(player, effectShow)
                editShowGui.open()
            }else{ // Add glow and add lore to confirm deletion
                val meta = event.currentItem!!.itemMeta!!
                meta.setEnchantmentGlintOverride(true)
                meta.lore = listOf(Colors.format(Colors.Color.ERROR.toString() + "Please click again to confirm deletion."))
                event.currentItem!!.itemMeta = meta
            }
        }

        if(event.slot == 49){ // Play only this effect
            val effectShow = EffectShow(showCategory, showName)
            effectShow.playOnly(id, null)

            player.closeInventory()
        }

    }

    override fun handleClose(event: InventoryCloseEvent) {
        val clickableComponent = TextComponent(TextComponent("Click here to re-open the edit effect gui."))
        clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
        clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/em editor $showCategory $showName $id")
        clickableComponent.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder("Click me to re-open the gui.").create())
        player.spigot().sendMessage(clickableComponent)
    }

    override fun setInventoryItems() {
        for(i in 0..35) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 36..53) inventory.setItem(i, GuiItems.getGrayPane())
        for(i in 10..16) inventory.setItem(i, null)
        for(i in 19..25) inventory.setItem(i, null)

        inventory.setItem(36, GuiItems.getDuplicate())
        inventory.setItem(38, GuiItems.getBack())
        inventory.setItem(42, GuiItems.getDelete())
        inventory.setItem(49, GuiItems.getPlay())

        updatePreview()
    }

    // Sets display item of current effect in 40th slot.
    private fun updatePreview() {
        val effectShow = EffectShow(showCategory, showName)
        val effect = effectShow.getEffect(id) ?: return

        if(effect.getDefaults().size > 14 * (page + 1)){
            inventory.setItem(32, GuiItems.getScrollFurther())
        }
        if(page > 0){
            inventory.setItem(30, GuiItems.getScrollBack())
        }

        val preview = ItemStack(effect.getDisplayMaterial())
        val previewMeta = preview.itemMeta!!
        val lore = ArrayList<String>()

        previewMeta.setDisplayName(Colors.format("#dcb5ff&l${
            effect.getIdentifier().lowercase()
            .replace("_", " ").replaceFirstChar(Char::titlecase)}"))
        lore.add(" ")
        effect.getDefaults().forEach {
            val parameter = it.name
            var value = effect.getSection(effectShow, id).get(parameter).toString()
            val sectionString = "${Colors.Color.BACKGROUND}$parameter: ${Colors.Color.DEFAULT}"

            if(value.length + parameter.length > 60){
                value = value.take(57 - parameter.length) + "..."
            }

            lore.add(Colors.format("&r#e0e0e0&o$sectionString") + value)
        }
        previewMeta.lore = lore
        previewMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

        preview.itemMeta = previewMeta

        inventory.setItem(40, preview)


        for(i in page * 14 until if(effect.getDefaults().size > (page + 1) * 14) (page + 1) * 14 else effect.getDefaults().size){
            val parameter = effect.getDefaults()[i]
            if(!parameter.name.equals("Type", ignoreCase = true)) {
                val item = ItemStack(Material.FILLED_MAP)
                val meta = item.itemMeta!!
                meta.setDisplayName(Colors.format("#dcb5ffEdit: ${parameter.name}"))
                lore.clear()

                // Format the description, so the lore isn't stretched out
                var lines = parameter.description.split("\\s+".toRegex()).map { "$it " }
                var line = ""
                for (word in lines) {
                    if (line.length + word.length > 50) {
                        lore.add(Colors.format(Colors.Color.BACKGROUND.toString() + line.trim()))
                        line = ""
                    }
                    line += word
                }
                if (line.isNotEmpty()) lore.add(Colors.format(Colors.Color.BACKGROUND.toString() + line.trim()))

                // Add an empty lore line between the description and current set value
                lore.add(" ")
                lore.add(Colors.format(Colors.Color.BACKGROUND.toString() + "Currently set to: "))

                // Format the current set value, so the lore isn't stretched out
                val currentValue = effect.getSection(effectShow, id).get(parameter.name).toString()
                if(!currentValue.contains(";")) {
                    lines = currentValue.split("\\s+".toRegex()).map { "$it " }
                    line = ""
                    for (word in lines) {
                        if (line.length + word.length > 50) {
                            lore.add(Colors.format(Colors.Color.DEFAULT.toString()) + line.trim())
                            line = ""
                        }
                        line += word
                    }
                    if (line.isNotEmpty()) lore.add(Colors.format(Colors.Color.DEFAULT.toString()) + line.trim())
                } else {
                    val currentValueArgs = currentValue.split(";")
                    currentValueArgs.forEach { lore.add(Colors.format(Colors.Color.DEFAULT.toString() + it + ";")) }
                }

                lore.addAll(listOf(
                    " ",
                    Colors.format(Colors.Color.BACKGROUND.toString() + "&oClick to edit")
                ))
                meta.lore = lore
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

                item.itemMeta = meta
                inventory.addItem(item)
            }
        }

    }

}