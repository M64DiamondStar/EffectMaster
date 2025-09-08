package me.m64diamondstar.effectmaster.editor.ui.show

import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.ktx.withoutItalics
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import me.m64diamondstar.effectmaster.utils.gui.Gui
import me.m64diamondstar.effectmaster.utils.items.GuiItems
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
            loopingMeta.displayName(
                emComponent(
                    (if (effectShow.looping) "<success>" else "<error>") +
                            "<bold><tiny>Looping ${if (effectShow.looping) "on" else "off"}"
                ).withoutItalics()
            )
            loopingMeta.lore(listOf(
                emComponent(
                            "<background>Click to toggle looping <bold>${if (!effectShow.looping) "on" else "off"}"
                ).withoutItalics()
            ))
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
            player.sendMessage(emComponent("<short_prefix><background>Enter the new looping delay in ticks (1 second = 20 ticks)."))
            player.sendMessage(emComponent(
                "<error>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:'Or click this text to cancel.'>" +
                        "To cancel this edit, please type <italic>cancel."
            ))
        }

        // Change duration
        if(event.slot == SettingSlot.LOOPING_INTERVAL) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "looping-interval")
            player.closeInventory()
            player.sendMessage(emComponent("<short_prefix><background>Enter the new looping interval in ticks (1 second = 20 ticks)."))
            player.sendMessage(emComponent(
                "<error>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:'Or click this text to cancel.'>" +
                        "To cancel this edit, please type <italic>cancel."
            ))
        }

        // Change center location
        if(event.slot == SettingSlot.CENTER_LOCATION) {
            SettingsPlayers.add(player, EffectShow(showCategory, showName), "center-location")
            player.closeInventory()
            player.sendMessage(emComponent("<short_prefix><background>Enter the new center location."))
            player.sendMessage(emComponent(
                "<error>" +
                        "<click:run_command:/em cancel>" +
                        "<hover:show_text:'Or click this text to cancel.'>" +
                        "To cancel this edit, please type <italic>cancel."
            ))
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        player.sendMessage(emComponent(
            "<click:run_command:'/em editor $showCategory $showName settings'>" +
                    "<default>Click here to re-open the show settings ui."
        ))
    }

    override fun setInventoryItems() {

        for(i in 0..26) inventory.setItem(i, GuiItems.getBlackPane())
        this.inventory.setItem(22, GuiItems.getBack())
        val effectShow = EffectShow(showCategory, showName)

        val loopingItem = ItemStack(Material.REPEATING_COMMAND_BLOCK)
        val loopingMeta = loopingItem.itemMeta!!
        loopingMeta.displayName(
            emComponent(
                (if(effectShow.looping) "<success>" else "<error>") +
                        "<bold><tiny>looping ${if (effectShow.looping) "on" else "off"}</tiny>"
            ).withoutItalics()
        )
        loopingMeta.lore(listOf(
            emComponent(
                        "<background>Click to toggle looping <bold>${if (!effectShow.looping) "on" else "off"}"
            ).withoutItalics()
        ))
        loopingMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingItem.itemMeta = loopingMeta
        this.inventory.setItem(SettingSlot.LOOPING_TOGGLE, loopingItem)


        val loopingDelayItem = ItemStack(Material.REDSTONE)
        val loopingDelayMeta = loopingDelayItem.itemMeta!!
        loopingDelayMeta.displayName(emComponent(
            "<primary_purple><bold><tiny>looping delay</tiny>: </bold><reset><#c9c9c9>${effectShow.loopingDelay}"
        ).withoutItalics())
        loopingDelayMeta.lore(listOf(
            emComponent("<background>Click to change the looping delay.").withoutItalics(),
            emComponent("<background>This is the delay after which the").withoutItalics(),
            emComponent("<background>looping will start after server startup.").withoutItalics()
        ))
        loopingDelayMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingDelayItem.itemMeta = loopingDelayMeta
        this.inventory.setItem(SettingSlot.LOOPING_DELAY, loopingDelayItem)


        val loopingIntervalItem = ItemStack(Material.REPEATER)
        val loopingIntervalMeta = loopingIntervalItem.itemMeta!!
        loopingIntervalMeta.displayName(emComponent(
            "<primary_purple><bold><tiny>looping interval</tiny>: </bold><reset><#c9c9c9>${effectShow.loopingInterval}"
        ).withoutItalics())
        loopingIntervalMeta.lore(listOf(
            emComponent("<background>Click to change the looping interval.").withoutItalics(),
            emComponent("<background>This is the time in ticks between").withoutItalics(),
            emComponent("<background>each loop.").withoutItalics()
        ))
        loopingIntervalMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        loopingIntervalItem.itemMeta = loopingIntervalMeta
        this.inventory.setItem(SettingSlot.LOOPING_INTERVAL, loopingIntervalItem)


        val centerLocationItem = ItemStack(Material.END_CRYSTAL)
        val centerLocationMeta = centerLocationItem.itemMeta!!
        centerLocationMeta.displayName(emComponent(
            "<primary_purple><bold><tiny>center location</tiny>: </bold><reset><#c9c9c9>" +
                    (LocationUtils.getStringFromLocation(effectShow.centerLocation, false, false) ?: "Not Set")
        ).withoutItalics())
        centerLocationMeta.lore(listOf(
            emComponent("<background>Click to change the center location.").withoutItalics(),
            emComponent("<background>This is the location of which all other").withoutItalics(),
            emComponent("<background>locations will be relative to if the").withoutItalics(),
            emComponent("<background>show is not played at the default location.").withoutItalics()
        ))
        centerLocationMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        centerLocationItem.itemMeta = centerLocationMeta
        this.inventory.setItem(SettingSlot.CENTER_LOCATION, centerLocationItem)
    }
}
