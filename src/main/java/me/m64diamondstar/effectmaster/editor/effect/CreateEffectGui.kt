package me.m64diamondstar.effectmaster.editor.effect

import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.utils.EditorUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
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
        return 45
    }

    override fun handleInventory(event: InventoryClickEvent) {
        if(event.currentItem == null) return

        if(event.slot == 41 && event.currentItem!!.type == Material.ARROW){
            val effectShow = EffectShow(showCategory, showName)
            val createEffectGui = CreateEffectGui(player, effectShow, page + 1)
            createEffectGui.open()
        }
        if(event.slot == 40){
            val editEffectShowGui = EditShowGui(event.whoClicked as Player, EffectShow(showCategory, showName))
            editEffectShowGui.open()
        }
        if(event.slot == 39 && event.currentItem!!.type == Material.ARROW){
            val effectShow = EffectShow(showCategory, showName)
            val createEffectGui = CreateEffectGui(player, effectShow, page - 1)
            createEffectGui.open()
        }

        if(event.slot !in 9..35) return

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
        val clickableComponent = TextComponent(TextComponent("Click here to re-open the create effect gui."))
        clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
        clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/em editor $showCategory $showName create")
        clickableComponent.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder("Click me to re-open the gui.").create())
        player.spigot().sendMessage(clickableComponent)
    }

    override fun setInventoryItems() {
        for(i in 0..8) inventory.setItem(i, GuiItems.getBlackPane())
        for(i in 36..44) inventory.setItem(i, GuiItems.getBlackPane())

        inventory.setItem(40, GuiItems.getBack())
        if(Effect.Type.getAllEffects().size > 27 * (page + 1)){
            inventory.setItem(41, GuiItems.getScrollFurther())
        }
        if(page > 0){
            inventory.setItem(39, GuiItems.getScrollBack())
        }

        val item = ItemStack(Material.STONE)
        val meta = item.itemMeta!!

        if(Effect.Type.getAllEffects().size > page * 27){

            for(i in page * 27 until Effect.Type.getAllEffects().size){
                val effect = Effect.Type.getAllEffects()[i]
                item.type = effect.getDisplayMaterial()
                meta.setDisplayName(Colors.format("#dcb5ff&l${effect.getIdentifier().lowercase().replace("_", " ")
                    .replaceFirstChar(Char::titlecase)}"))
                val description = effect.getDescription()
                val lore = mutableListOf<String>()
                var line = ""
                for(word in description.split(" ")){
                    if(line.length + word.length + 1 <= 40){
                        line += if(line.isEmpty()) word else " $word"
                    }else{
                        lore.add(Colors.format(Colors.Color.BACKGROUND.toString() + line.trim()))
                        line = word
                    }
                }
                if(line.isNotEmpty()) lore.add(Colors.format(Colors.Color.BACKGROUND.toString() + line.trim()))
                meta.lore = lore.toList()
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

                item.itemMeta = meta
                inventory.addItem(TypeData.setIdentifier(item, effect.getIdentifier()))

            }
        }
    }
}