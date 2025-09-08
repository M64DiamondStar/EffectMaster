package me.m64diamondstar.effectmaster.editor.listeners

import me.m64diamondstar.effectmaster.editor.ui.show.ShowSettingsGui
import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class SettingsChatListener: Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent){
        val player = event.player
        val message = event.message

        if(!SettingsPlayers.contains(player)) return

        event.isCancelled = true

        val showCategory = SettingsPlayers.get(player)!!.first.getCategory()
        val showName = SettingsPlayers.get(player)!!.first.getName()
        val effectShow = EffectShow(showCategory, showName)
        val setting = SettingsPlayers.get(player)!!.second

        if(event.message.equals("cancel", ignoreCase = true)){
            player.sendMessage(emComponent("<prefix><success>Cancelled edit."))
            val showSettingsGui = ShowSettingsGui(player, effectShow)
            showSettingsGui.open()
            SettingsPlayers.remove(player)
        }

        when(setting) {
            "looping-delay" -> {
                val num = message.toLongOrNull()
                if(num == null || num < 1) {
                    player.sendMessage(emComponent("<short_prefix><error>You're editing the looping delay. You must enter a number bigger or equal to 1."))
                    return
                }
                effectShow.loopingDelay = event.message.toLong()
                player.sendMessage(emComponent("<short_prefix><success>The delay has been set to ${event.message} ticks."))

                // Update show looper
                val effectShow = EffectShow(showCategory, showName)
                ShowLooper.updateLoop(effectShow)
            }

            "looping-interval" -> {
                val num = message.toLongOrNull()
                if(num == null || num < 1) {
                    player.sendMessage(emComponent("<short_prefix><error>You're editing the looping interval. You must enter a number bigger or equal to 1."))
                    return
                }
                effectShow.loopingInterval = event.message.toLong()
                player.sendMessage(emComponent("<short_prefix><success>The interval has been set to ${event.message} ticks."))

                // Update show looper
                val effectShow = EffectShow(showCategory, showName)
                ShowLooper.updateLoop(effectShow)
            }

            "center-location" -> {
                if(LocationUtils.getLocationFromString(event.message) == null){
                    player.sendMessage(emComponent("<short_prefix><error>You're editing the center location. You must enter a valid location."))
                    return
                }

                effectShow.centerLocation = LocationUtils.getLocationFromString(event.message)
                player.sendMessage(emComponent("<short_prefix><success>The center location has been set to ${event.message}."))
            }
        }
        val showSettingsGui = ShowSettingsGui(player, effectShow)
        showSettingsGui.open()
        SettingsPlayers.remove(player)
    }

}