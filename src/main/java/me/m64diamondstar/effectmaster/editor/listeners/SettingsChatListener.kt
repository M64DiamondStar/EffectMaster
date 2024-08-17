package me.m64diamondstar.effectmaster.editor.listeners

import me.m64diamondstar.effectmaster.editor.show.ShowSettingsGui
import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
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
            player.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Cancelled edit."))
            val showSettingsGui = ShowSettingsGui(player, effectShow)
            showSettingsGui.open()
            SettingsPlayers.remove(player)
        }

        when(setting) {
            "looping-delay" -> {
                if(message.toLongOrNull() == null) {
                    player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toShortString() + "You're editing the looping delay. You must enter a number."))
                    return
                }
                effectShow.loopingDelay = event.message.toLong()
                player.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toShortString() + "The delay has been set to ${event.message} ticks."))

                // Update show looper
                val effectShow = EffectShow(showCategory, showName)
                ShowLooper.updateLoop(effectShow)
            }

            "looping-interval" -> {
                if(message.toLongOrNull() == null) {
                    player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toShortString() + "You're editing the looping interval. You must enter a number."))
                    return
                }
                effectShow.loopingInterval = event.message.toLong()
                player.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toShortString() + "The interval has been set to ${event.message} ticks."))

                // Update show looper
                val effectShow = EffectShow(showCategory, showName)
                ShowLooper.updateLoop(effectShow)
            }
        }
        val showSettingsGui = ShowSettingsGui(player, effectShow)
        showSettingsGui.open()
        SettingsPlayers.remove(player)
    }

}