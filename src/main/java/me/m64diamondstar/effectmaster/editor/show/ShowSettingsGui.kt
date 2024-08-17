package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ShowSettingsGui(private val player: Player, effectShow: EffectShow): Gui(player = player) {

    private val showCategory: String = effectShow.getCategory()
    private val showName: String = effectShow.getName()

    override fun setDisplayName(): String {
        return "Editing $showName's settings..."
    }

    override fun setSize(): Int {
        return 27
    }

    override fun handleInventory(event: InventoryClickEvent) {

        // Back to the main menu
        if(event.slot == 22){
            val effectShow = EffectShow(showCategory, showName)
            val editShowGui = EditShowGui(player, effectShow)
            editShowGui.open()
        }

        // Toggle looping
        if(event.slot == 11){
            val effectShow = EffectShow(showCategory, showName)
            effectShow.looping = !effectShow.looping

            // Update looping item
            val loopingItem = ItemStack(Material.REPEATING_COMMAND_BLOCK)
            val loopingMeta = loopingItem.itemMeta!!
            loopingMeta.setDisplayName(Colors.format((if(effectShow.looping) Colors.Color.SUCCESS.toString() else Colors.Color.ERROR.toString()) + "&lLooping " + (if (effectShow.looping) "on" else "off")))
            loopingMeta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Click to toggle looping &l${if (!effectShow.looping) "on" else "off"}"))
            loopingMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            loopingItem.itemMeta = loopingMeta
            this.inventory.setItem(11, loopingItem)

            // Update show loop
            if(effectShow.looping)
                ShowLooper.loop(effectShow)
            else
                ShowLooper.stopLoop(effectShow)
        }

        // Change looping delay
        if(event.slot == 13) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "looping-delay")
            player.closeInventory()
            player.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "Enter the new looping delay in ticks (1 second = 20 ticks)."))
            player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toShortString() + "To cancel this edit, please type &ocancel"))
        }

        // Change duration
        if(event.slot == 15) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "looping-interval")
            player.closeInventory()
            player.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "Enter the new looping interval in ticks (1 second = 20 ticks)."))
            player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toShortString() + "To cancel this edit, please type &ocancel"))
        }
    }

    override fun setInventoryItems() {

        for(i in 0..26) inventory.setItem(i, GuiItems.getBlackPane())
        this.inventory.setItem(22, GuiItems.getBack())
        val effectShow = EffectShow(showCategory, showName)

        val loopingItem = ItemStack(Material.REPEATING_COMMAND_BLOCK)
        val loopingMeta = loopingItem.itemMeta!!
        loopingMeta.setDisplayName(Colors.format((if(effectShow.looping) Colors.Color.SUCCESS.toString() else Colors.Color.ERROR.toString()) + "&lLooping " + (if (effectShow.looping) "on" else "off")))
        loopingMeta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Click to toggle looping &l${if (!effectShow.looping) "on" else "off"}"))
        loopingMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingItem.itemMeta = loopingMeta
        this.inventory.setItem(11, loopingItem)

        player.health

        val loopingDelayItem = ItemStack(Material.REDSTONE)
        val loopingDelayMeta = loopingDelayItem.itemMeta!!
        loopingDelayMeta.setDisplayName(Colors.format("#5cbf9c&lLooping delay: &r#5cbf9c" + effectShow.loopingDelay))
        loopingDelayMeta.lore = listOf(
            Colors.format(Colors.Color.BACKGROUND.toString() + "Click to change the looping delay."),
            Colors.format(Colors.Color.BACKGROUND.toString() + "This is the delay after which the"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "looping will start after server startup.")
        )
        loopingDelayMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingDelayItem.itemMeta = loopingDelayMeta
        this.inventory.setItem(13, loopingDelayItem)



        val loopingIntervalItem = ItemStack(Material.REPEATER)
        val loopingIntervalMeta = loopingIntervalItem.itemMeta!!
        loopingIntervalMeta.setDisplayName(Colors.format("#5cbf9c&lLooping interval: &r#5cbf9c" + effectShow.loopingInterval))
        loopingIntervalMeta.lore = listOf(
            Colors.format(Colors.Color.BACKGROUND.toString() + "Click to change the looping interval."),
            Colors.format(Colors.Color.BACKGROUND.toString() + "This is the time in ticks between"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "each loop.")
        )
        loopingIntervalMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingIntervalItem.itemMeta = loopingIntervalMeta
        this.inventory.setItem(15, loopingIntervalItem)
    }
}