package me.m64diamondstar.effectmaster.editor.listeners

import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ParameterChatListener: Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncPlayerChatEvent){
        val player = event.player

        if(!EditingPlayers.contains(player)) return

        event.isCancelled = true

        val showCategory = EditingPlayers.get(player)!!.first.getCategory()
        val showName = EditingPlayers.get(player)!!.first.getName()
        val effectShow = EffectShow(showCategory, showName)

        val id = EditingPlayers.get(player)!!.second
        val parameter = EditingPlayers.get(player)!!.third
        val effect = effectShow.getEffect(id)

        if(event.message.equals("cancel", ignoreCase = true)){
            player.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Cancelled edit."))
            val editEffectGui = EditEffectGui(player, id, effectShow, 0)
            editEffectGui.open()
            EditingPlayers.remove(player)
        }else{

            val value = event.message

            if(effect?.getDefaults()?.find { it.name == parameter }?.parameterValidator?.isValid(value) == true){
                effectShow.getEffect(id)!!.getSection(effectShow, id).set(parameter, effect.getDefaults().find { it.name == parameter }?.parameterTypeConverter?.getAsType(value))
                effectShow.reloadConfig()

                player.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Edited parameter."))
                val editEffectGui = EditEffectGui(player, id, effectShow, 0)
                editEffectGui.open()
                EditingPlayers.remove(player)
            }else{
                player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The value entered is not possible."))
                player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You need to enter a(n) $parameter, please read " +
                        "the info above."))
                player.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "Entered value: '$value'"))
            }
        }

    }

}