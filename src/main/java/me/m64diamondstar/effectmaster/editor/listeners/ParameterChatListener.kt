package me.m64diamondstar.effectmaster.editor.listeners

import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
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
            player.sendMessage(emComponent("<prefix><success>Cancelled edit."))
            val editEffectGui = EditEffectGui(player, id, effectShow, 0)
            editEffectGui.open()
            EditingPlayers.remove(player)
        }else{

            val value = event.message

            if(effect?.getDefaults()?.find { it.name == parameter.name }?.parameterValidator?.isValid(value) == true){
                effectShow.getEffect(id)!!.getSection(effectShow, id).set(parameter.name, effect.getDefaults().find { it.name == parameter.name }?.parameterTypeConverter?.getAsType(value))
                effectShow.saveConfig()

                player.sendMessage(emComponent("<prefix><success>Edited parameter."))
                val editEffectGui = EditEffectGui(player, id, effectShow, 0)
                editEffectGui.open()
                EditingPlayers.remove(player)
            }else{
                player.sendMessage(emComponent("<prefix><error>The value entered is not possible."))
                player.sendMessage(emComponent("<prefix><error>You need to enter a(n) $parameter, please read " +
                        "the info above."))
                player.sendMessage(emComponent("<prefix><error>Entered value: '$value'"))
            }
        }

    }



}