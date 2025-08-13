package me.m64diamondstar.effectmaster.editor.show

import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
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
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ShowSettingsGui(private val player: Player, effectShow: EffectShow): Gui(player = player) {

    private object SettingSlot {
        const val LOOPING_TOGGLE: Int = 10
        const val LOOPING_DELAY: Int = 12
        const val LOOPING_INTERVAL: Int = 14
        const val CENTER_LOCATION: Int = 16
    }

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
        if(event.slot == SettingSlot.LOOPING_TOGGLE){
            val effectShow = EffectShow(showCategory, showName)
            effectShow.looping = !effectShow.looping

            // Update looping item
            val loopingItem = ItemStack(Material.REPEATING_COMMAND_BLOCK)
            val loopingMeta = loopingItem.itemMeta!!
            loopingMeta.setDisplayName(Colors.format((if(effectShow.looping) Colors.Color.SUCCESS.toString() else Colors.Color.ERROR.toString()) + "&lLooping " + (if (effectShow.looping) "on" else "off")))
            loopingMeta.lore = listOf(Colors.format(Colors.Color.BACKGROUND.toString() + "Click to toggle looping &l${if (!effectShow.looping) "on" else "off"}"))
            loopingMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            loopingItem.itemMeta = loopingMeta
            this.inventory.setItem(SettingSlot.LOOPING_TOGGLE, loopingItem)

            // Update show loop
            if(effectShow.looping)
                ShowLooper.loop(effectShow)
            else
                ShowLooper.stopLoop(effectShow)
        }

        // Change looping delay
        if(event.slot == SettingSlot.LOOPING_DELAY) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "looping-delay")
            player.closeInventory()
            player.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "Enter the new looping delay in ticks (1 second = 20 ticks)."))
            (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize(
                "<${Colors.Color.ERROR}>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:Or click this text to cancel.>" +
                        "To cancel this edit, please type <italic>cancel."))
        }

        // Change duration
        if(event.slot == SettingSlot.LOOPING_INTERVAL) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "looping-interval")
            player.closeInventory()
            player.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "Enter the new looping interval in ticks (1 second = 20 ticks)."))
            (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize(
                "<${Colors.Color.ERROR}>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:Or click this text to cancel.>" +
                        "To cancel this edit, please type <italic>cancel."))
        }

        // Change center location
        if(event.slot == SettingSlot.CENTER_LOCATION) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "center-location")
            player.closeInventory()
            player.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "Enter the new center location."))
            (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize(
                "<${Colors.Color.ERROR}>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:Or click this text to cancel.>" +
                        "To cancel this edit, please type <italic>cancel."))
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        val clickableComponent = TextComponent(TextComponent("Click here to re-open the edit gui."))
        clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
        clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/em editor $showCategory $showName settings")
        clickableComponent.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder("Click me to re-open the edit gui.").create())
        player.spigot().sendMessage(clickableComponent)
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
        this.inventory.setItem(SettingSlot.LOOPING_TOGGLE, loopingItem)

        player.health

        val loopingDelayItem = ItemStack(Material.REDSTONE)
        val loopingDelayMeta = loopingDelayItem.itemMeta!!
        loopingDelayMeta.setDisplayName(Colors.format("#5cbf9c&lLooping delay: &r#c9c9c9" + effectShow.loopingDelay))
        loopingDelayMeta.lore = listOf(
            Colors.format(Colors.Color.BACKGROUND.toString() + "Click to change the looping delay."),
            Colors.format(Colors.Color.BACKGROUND.toString() + "This is the delay after which the"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "looping will start after server startup.")
        )
        loopingDelayMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingDelayItem.itemMeta = loopingDelayMeta
        this.inventory.setItem(SettingSlot.LOOPING_DELAY, loopingDelayItem)



        val loopingIntervalItem = ItemStack(Material.REPEATER)
        val loopingIntervalMeta = loopingIntervalItem.itemMeta!!
        loopingIntervalMeta.setDisplayName(Colors.format("#5cbf9c&lLooping interval: &r#c9c9c9" + effectShow.loopingInterval))
        loopingIntervalMeta.lore = listOf(
            Colors.format(Colors.Color.BACKGROUND.toString() + "Click to change the looping interval."),
            Colors.format(Colors.Color.BACKGROUND.toString() + "This is the time in ticks between"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "each loop.")
        )
        loopingIntervalMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingIntervalItem.itemMeta = loopingIntervalMeta
        this.inventory.setItem(SettingSlot.LOOPING_INTERVAL, loopingIntervalItem)



        val centerLocationItem = ItemStack(Material.END_CRYSTAL)
        val centerLocationMeta = centerLocationItem.itemMeta!!
        centerLocationMeta.setDisplayName(Colors.format("#5cbf9c&lCenter location: &r#c9c9c9" +
                (LocationUtils.getStringFromLocation(effectShow.centerLocation, false, false) ?: "Not Set")))
        centerLocationMeta.lore = listOf(
            Colors.format(Colors.Color.BACKGROUND.toString() + "Click to change the center location."),
            Colors.format(Colors.Color.BACKGROUND.toString() + "This is the location of which all other"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "locations will be relative to if the"),
            Colors.format(Colors.Color.BACKGROUND.toString() + "show is not played at the default location.")
        )
        centerLocationMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        centerLocationItem.itemMeta = centerLocationMeta
        this.inventory.setItem(SettingSlot.CENTER_LOCATION, centerLocationItem)
    }
}