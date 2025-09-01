package me.m64diamondstar.effectmaster.editor.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.ktx.withoutItalics
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import me.m64diamondstar.effectmaster.utils.items.TypeData
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class PresetEffectGui(private val player: Player, private val effectShow: EffectShow, private val page: Int, private val effect: Effect): Gui(player = player) {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()
    private val presets = EffectMaster.getEffectPresets().getAllPresets(effect.getIdentifier())

    private var deleteSlot: Int? = null
    private var deleteLore: List<Component>? = null

    override fun setDisplayName(): String {
        return "Creating effect for $showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {
        if(event.currentItem == null) return

        if(event.slot == 50 && event.currentItem!!.type == Material.ARROW){
            val presetEffectGui = PresetEffectGui(player, effectShow, page + 1, effect)
            presetEffectGui.open()
        }
        if(event.slot == 49){
            val editEffectShowGui = CreateEffectGui(player, effectShow, 0)
            editEffectShowGui.open()
        }
        if(event.slot == 48 && event.currentItem!!.type == Material.ARROW){
            val presetEffectGui = PresetEffectGui(player, effectShow, page - 1, effect)
            presetEffectGui.open()
        }

        if(event.slot !in 9..44) return

        presets.forEach { preset ->
            if(TypeData.getIdentifier(event.currentItem!!) == preset.name){

                if(event.isLeftClick) {
                    val id = effectShow.getMaxId() + 1
                    val defaults = effect.getDefaults()

                    val filteredValues = preset.values.filter {pair -> defaults.map { it.name }.contains(pair.first) }

                    val parameters = mutableListOf<ParameterLike>()
                    parameters.add(Parameter(
                        "Type",
                        effect.getIdentifier(),
                        "",
                        { it }
                    ) { true })

                    parameters.addAll(
                        filteredValues.map { (name, value) ->
                            Parameter(
                                name,
                                value,
                                "",
                                defaults.find { it.name == name }!!.parameterTypeConverter
                            ) { true }
                        }
                    )

                    effectShow.setDefaults(id, parameters)

                    val editEffectShowGui = EditShowGui(player, EffectShow(showCategory, showName))
                    editEffectShowGui.open()
                }

                else { // Delete preset
                    if(event.currentItem?.itemMeta?.hasEnchantmentGlintOverride() == true) {
                        EffectMaster.getEffectPresets().removePreset(effect.getIdentifier(), preset.name)
                        val presetEffectGui = PresetEffectGui(player, effectShow, page, effect)
                        presetEffectGui.open()
                    } else {
                        val currentDeleteSlot = deleteSlot
                        if(currentDeleteSlot != null){ // Remove other deletion session if there is one
                            val item = event.inventory.getItem(currentDeleteSlot) ?: return
                            val meta = item.itemMeta ?: return
                            meta.lore(deleteLore)
                            meta.setEnchantmentGlintOverride(false)
                            item.setItemMeta(meta)
                        }

                        val meta = event.currentItem!!.itemMeta!!
                        deleteLore = meta.lore()
                        deleteSlot = event.slot

                        meta.setEnchantmentGlintOverride(true)
                        meta.lore(listOf(emComponent("<error>Please right-click again to confirm deletion.")))
                        event.currentItem!!.itemMeta = meta
                    }
                }

            }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        player.sendMessage(emComponent(
            "<click:run_command:'/em editor $showCategory $showName create'>" +
                    "<default>Click here to re-open the effect creation ui."
        ))
    }

    override fun setInventoryItems() {
        for(i in 0..8) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 45..53) inventory.setItem(i, GuiItems.getBlackPane())

        inventory.setItem(49, GuiItems.getBack())
        if(presets.size > 36 * (page + 1)){
            inventory.setItem(50, GuiItems.getScrollFurther())
        }
        if(page > 0){
            inventory.setItem(48, GuiItems.getScrollBack())
        }

        if(presets.isEmpty()){
            inventory.setItem(22, GuiItems.getEmptySign())
            return
        }

        if(presets.size > page * 36){

            for(i in page * 36 until presets.size){
                val preset = presets[i]
                val item = ItemStack(preset.material)
                val meta = item.itemMeta ?: continue
                meta.displayName(emComponent("<primary_purple><tiny><b>" +
                        preset.name.lowercase().replace("_", " ")
                ).withoutItalics())
                val description = preset.values
                val lore = mutableListOf<Component>(Component.empty())

                description.forEach { (parameter, originalValue) ->
                    var value = originalValue
                    val sectionString = "<background>$parameter: <default>"

                    if(value.length + parameter.length > 60){
                        value = value.take(57 - parameter.length) + "..."
                    }

                    lore.add(emComponent("$sectionString$value").withoutItalics())
                }

                lore.add(Component.empty())
                lore.add(emComponent("<default>Left Click to use this preset").withoutItalics())
                lore.add(emComponent("<default>Right Click to <error>delete</error> this preset").withoutItalics())

                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

                item.itemMeta = meta
                inventory.addItem(TypeData.setIdentifier(item, preset.name))
            }
        }
    }
}