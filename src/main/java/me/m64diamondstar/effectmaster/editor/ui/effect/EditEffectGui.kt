package me.m64diamondstar.effectmaster.editor.ui.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.editor.ui.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.utils.ChatSession
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.ktx.plainText
import me.m64diamondstar.effectmaster.ktx.sync
import me.m64diamondstar.effectmaster.ktx.withoutItalics
import me.m64diamondstar.effectmaster.shows.EffectPresets
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class EditEffectGui(private val player: Player, private val id: Int, private val effectShow: EffectShow, private val page: Int = 0): Gui(player = player) {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()

    override fun setDisplayName(): String {
        return "Editing effect $id"
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {

        // Check if player clicks in upper inventory
        if(event.clickedInventory!!.type != InventoryType.CHEST) return

        if(event.slot == 5 && event.currentItem!!.type == Material.TIPPED_ARROW){ // 'Edit' is clicked
            val editEffectGui = EditEffectGui(player, id + 1, EffectShow(showCategory, showName))
            editEffectGui.open()
        }

        if(event.slot == 3 && event.currentItem!!.type == Material.TIPPED_ARROW){ // 'Edit' is clicked
            val editEffectGui = EditEffectGui(player, id - 1, EffectShow(showCategory, showName))
            editEffectGui.open()
        }

        if(event.slot in 10..16 || event.slot in 19..25){
            val item = event.currentItem!!
            val meta = item.itemMeta!!
            val effect = effectShow.getEffect(id)!! // Player can't be editing an effect which is null

            try{
                val parameter = effect.getDefaults().find { meta.displayName()?.plainText()?.split(": ")[1] == it.name } ?: return
                val description = parameter.description
                val current = meta.displayName()?.plainText()?.split(": ")[1]

                if(!event.isShiftClick) {
                    player.sendMessage(emComponent("<prefix><default>Parameter description:"))
                    player.sendMessage(emComponent("<background>$description"))

                    if (current != null)
                        player.sendMessage(
                            emComponent(
                                "<br>" +
                                        "<default>" +
                                        "<click:copy_to_clipboard:'${
                                            effect.getSection(effectShow, id).getString(current)
                                        }'>" +
                                        "<hover:show_text:'${effect.getSection(effectShow, id).getString(current)}'>" +
                                        "Click here to copy the current value.<br>"
                            )
                        )

                    player.sendMessage(
                        emComponent(
                            "<error>" +
                                    "<click:run_command:/em cancel>" +
                                    "<hover:show_text:Or click this text to cancel.>" +
                                    "To cancel this edit, please type <italic>cancel."
                        )
                    )

                    EditingPlayers.add(player, EffectShow(showCategory, showName), id, parameter)
                } else {
                    if(current != null)
                        player.sendMessage(emComponent(
                                "<default>" +
                                "<click:copy_to_clipboard:'${effect.getSection(effectShow, id).getString(current)}'>" +
                                "<click:run_command:'/em editor $showCategory $showName $id'>" +
                                "<hover:show_text:'${effect.getSection(effectShow, id).getString(current)}'>" +
                                "Click here to copy the current value of the ${parameter.name} parameter."))
                }

                closeInventoryWithoutHandle(player)
            }catch (_: IllegalArgumentException){
                closeInventoryWithoutHandle(player)
                player.sendMessage(emComponent("<short_prefix><error>This parameter type does not exist."))
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
            val editShowGui = EditShowGui(player, effectShow)
            editShowGui.open()
        }

        if(event.slot == 42){ // 'Delete' is clicked
            if(event.currentItem!!.itemMeta!!.hasEnchantmentGlintOverride()){ // Already clicked once.
                effectShow.deleteEffect(id)

                val editShowGui = EditShowGui(player, effectShow)
                editShowGui.open()
            }else{ // Add glow and add lore to confirm deletion
                val meta = event.currentItem!!.itemMeta!!
                meta.setEnchantmentGlintOverride(true)
                meta.lore(listOf(emComponent("<error>Please click again to confirm deletion.")))
                event.currentItem!!.itemMeta = meta
            }
        }

        if(event.slot == 40){ // Play only this effect
            effectShow.playOnly(id, null)
            player.closeInventory()
        }

        if(event.slot == 44) {
            val effectIdentifier = effectShow.getEffect(id)?.getIdentifier() ?: return
            val presets =  EffectMaster.getEffectPresets()
            closeInventoryWithoutHandle(player)
            ChatSession.prompt(
                player = player,
                prompt = emComponent("<prefix><default>Enter a preset name (letters, numbers, _ or - only)."),
                validator = { input ->
                    input.matches(Regex("^[a-zA-Z0-9_-]+$")) && presets.getPreset(effectIdentifier, input) == null // Make sure it doesn't exist already
                },
                onComplete = { value ->
                    sync {

                        player.sendMessage(emComponent("<prefix><success>Created a new preset called <i>$value</i>."))
                        ChatSession.prompt(
                            player = player,
                            prompt = emComponent("<prefix><default>Enter a material to use for the preset (press tab to view all options)."),
                            validator = { input ->
                                Material.entries.map { it.name }.contains(input.uppercase())
                            },
                            onComplete = { materialName ->
                                sync {
                                    val material = Material.getMaterial(materialName.uppercase())!! // Can't be null because of the validator
                                    player.sendMessage(emComponent("<prefix><success>Created a new preset called <i>$value</i> with the material ${materialName.lowercase()}."))

                                    // Add preset internally
                                    presets.addPreset(EffectPresets.Preset(
                                        name = value,
                                        effectType = effectIdentifier,
                                        material = material,
                                        values = effectShow.getEffect(id)?.getSection(effectShow, id)?.getKeys(false)?.map { key ->
                                            key!! to effectShow.getEffect(id)!!.getSection(effectShow, id).getString(key)!!
                                        } ?: emptyList()
                                    ))

                                    // re-open UI after creation is complete
                                    val editEffectGui = EditEffectGui(player, id, effectShow, page)
                                    editEffectGui.open()
                                }
                            },
                            onCancel = {
                                // re-open UI after creation is cancelled
                                val editEffectGui = EditEffectGui(player, id, effectShow, page)
                                editEffectGui.open()
                            },
                            tabCompletions = Material.entries.map { it.name.lowercase() }
                        )
                    }
                },
                onCancel = {
                    // re-open UI after creation is cancelled
                    val editEffectGui = EditEffectGui(player, id, effectShow, page)
                    editEffectGui.open()
                },
            )
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        player.sendMessage(
            emComponent(
            "<click:run_command:'/em editor $showCategory $showName $id'>" +
                    "<background>Click here to re-open the effect editor ($showName, effect $id)."
        ))
    }

    override fun setInventoryItems() {
        for(i in 0..35) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 36..53) inventory.setItem(i, GuiItems.getGrayPane())
        for(i in 10..16) inventory.setItem(i, null)
        for(i in 19..25) inventory.setItem(i, null)

        inventory.setItem(36, GuiItems.getDuplicate())
        inventory.setItem(38, GuiItems.getBack())
        inventory.setItem(40, GuiItems.getPlay("effect"))
        inventory.setItem(42, GuiItems.getDeleteEffect())
        inventory.setItem(44, GuiItems.getSaveAsPreset())

        updatePreview()
    }

    // Sets display item of current effect in 40th slot.
    private fun updatePreview() {
        val effect = effectShow.getEffect(id) ?: return

        if(effect.getDefaults().size > 14 * (page + 1)){
            inventory.setItem(32, GuiItems.getScrollFurther())
        }
        if(page > 0){
            inventory.setItem(30, GuiItems.getScrollBack())
        }
        if(effectShow.getMaxId() > id){ // Player is able to scroll to the next effect
            inventory.setItem(5, GuiItems.getNextEffect())
        }
        if(id > 1){ // Player is able to scroll to the previous effect
            inventory.setItem(3, GuiItems.getPreviousEffect())
        }

        val preview = ItemStack(effect.getDisplayMaterial())
        val previewMeta = preview.itemMeta!!
        val lore = ArrayList<Component>()

        previewMeta.displayName(emComponent("<primary_purple><tiny><b>${
            effect.getIdentifier().lowercase().replace("_", " ")}").withoutItalics())
        lore.add(emComponent(" "))
        effect.getDefaults().forEach {
            val parameter = it.name
            var value = effect.getSection(effectShow, id).get(parameter).toString()
            val sectionString = "<background>$parameter: <default>"

            if(value.length + parameter.length > 60){
                value = value.take(57 - parameter.length) + "..."
            }

            lore.add(emComponent("$sectionString$value").withoutItalics())
        }
        previewMeta.lore(lore)
        previewMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

        preview.itemMeta = previewMeta

        inventory.setItem(31, preview)

        for(i in page * 14 until if(effect.getDefaults().size > (page + 1) * 14) (page + 1) * 14 else effect.getDefaults().size){
            val parameter = effect.getDefaults()[i]
            if(!parameter.name.equals("Type", ignoreCase = true)) {
                val item = ItemStack(Material.FILLED_MAP)
                val meta = item.itemMeta!!
                meta.displayName(emComponent("<#dcb5ff>Edit: ${parameter.name}").withoutItalics())
                val lore = ArrayList<Component>()

                // Format the description, so the lore isn't stretched out
                var lines = parameter.description.split("\\s+".toRegex()).map { "$it " }
                var line = ""
                for (word in lines) {
                    if (line.length + word.length > 50) {
                        lore.add(emComponent("<background>" + line.trim()).withoutItalics())
                        line = ""
                    }
                    line += word
                }
                if (line.isNotEmpty()) lore.add(emComponent("<background>" + line.trim()))

                // Add an empty lore line between the description and current set value
                lore.add(emComponent(" "))
                lore.add(emComponent("<background>Currently set to: ").withoutItalics())

                // Format the current set value, so the lore isn't stretched out
                val currentValue = effect.getSection(effectShow, id).get(parameter.name).toString()
                if(!currentValue.contains(";")) {
                    lines = currentValue.split("\\s+".toRegex()).map { "$it " }
                    line = ""
                    for (word in lines) {
                        if (line.length + word.length > 50) {
                            lore.add(emComponent("<default>" + line.trim()).withoutItalics())
                            line = ""
                        }
                        line += word
                    }
                    if (line.isNotEmpty()) lore.add(emComponent("<default>" + line.trim()).withoutItalics())
                } else {
                    val currentValueArgs = currentValue.split(";")
                    currentValueArgs.forEach { lore.add(emComponent("<default>$it;").withoutItalics()) }
                }

                lore.addAll(listOf(
                    Component.empty(),
                    emComponent("<background>Click to edit").withoutItalics(),
                    emComponent("<background>Shift + click to copy").withoutItalics()
                ))
                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

                item.itemMeta = meta
                inventory.addItem(item)
            }
        }

    }

}