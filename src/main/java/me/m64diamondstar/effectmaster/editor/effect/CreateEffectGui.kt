package me.m64diamondstar.effectmaster.editor.effect

import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.utils.EditorUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import me.m64diamondstar.effectmaster.utils.items.TypeData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class CreateEffectGui(private val player: Player, effectShow: EffectShow, private val page: Int): Gui(player = player) {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()

    override fun setDisplayName(): String {
        return "Creating effect for $showName..."
    }

    override fun setSize(): Int {
        return 54
    }

    override fun handleInventory(event: InventoryClickEvent) {
        if(event.currentItem == null) return

        if(event.slot == 50 && event.currentItem!!.type == Material.ARROW){
            val effectShow = EffectShow(showCategory, showName)
            val createEffectGui = CreateEffectGui(player, effectShow, page + 1)
            createEffectGui.open()
        }
        if(event.slot == 49){
            val editEffectShowGui = EditShowGui(event.whoClicked as Player, EffectShow(showCategory, showName))
            editEffectShowGui.open()
        }
        if(event.slot == 48 && event.currentItem!!.type == Material.ARROW){
            val effectShow = EffectShow(showCategory, showName)
            val createEffectGui = CreateEffectGui(player, effectShow, page - 1)
            createEffectGui.open()
        }

        if(event.slot !in 9..44) return

        Effect.Type.getAllEffects().forEach {
            if(TypeData.getIdentifier(event.currentItem!!) == it.getIdentifier()){

                val effectShow = EffectShow(showCategory, showName)
                val id = effectShow.getMaxId() + 1
                effectShow.setDefaults(id, EditorUtils.filterPlayerDefaults(player, it))

                val editEffectShowGui = EditShowGui(player, EffectShow(showCategory, showName))
                editEffectShowGui.open()

            }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize(
            "<click:run_command:'/em editor $showCategory $showName create'>" +
                    "<${Colors.Color.BACKGROUND}>Click here to re-open effect creation ui."
        ))
    }

    override fun setInventoryItems() {
        for(i in 0..8) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 45..53) inventory.setItem(i, GuiItems.getBlackPane())

        inventory.setItem(49, GuiItems.getBack())
        if(Effect.Type.getAllEffects().size > 36 * (page + 1)){
            inventory.setItem(50, GuiItems.getScrollFurther())
        }
        if(page > 0){
            inventory.setItem(48, GuiItems.getScrollBack())
        }

        if(Effect.Type.getAllEffects().size > page * 36){

            for(i in page * 36 until Effect.Type.getAllEffects().size){
                val effect = Effect.Type.getAllEffects()[i]
                val item = ItemStack(effect.getDisplayMaterial())
                val meta = item.itemMeta ?: continue
                meta.displayName(MiniMessage.miniMessage().deserialize(Colors.format("#dcb5ff&l${effect.getIdentifier().lowercase().replace("_", " ")
                    .replaceFirstChar(Char::titlecase)}")))
                val description = effect.getDescription()
                val lore = mutableListOf<Component>()
                var line = ""
                for(word in description.split(" ")){
                    if(line.length + word.length + 1 <= 40){
                        line += if(line.isEmpty()) word else " $word"
                    }else{
                        lore.add(MiniMessage.miniMessage().deserialize(Colors.format(Colors.Color.BACKGROUND.toString() + line.trim())))
                        line = word
                    }
                }
                if(line.isNotEmpty()) lore.add(MiniMessage.miniMessage().deserialize(Colors.format(Colors.Color.BACKGROUND.toString() + line.trim())))
                meta.lore(lore)
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

                item.itemMeta = meta
                inventory.addItem(TypeData.setIdentifier(item, effect.getIdentifier()))

            }
        }
    }
}