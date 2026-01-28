package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.ui.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.ui.show.ShowSettingsGui
import me.m64diamondstar.effectmaster.editor.utils.ChatSession
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EnterSubCommand: SubCommand {

    override fun getName(): String {
        return "enter"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(emComponent("<prefix><error>You can only use this command as a player."))
            return
        }

        if(EditingPlayers.contains(sender)) {
            onEditParameter(sender, args)
        } else if(SettingsPlayers.contains(sender)) {
            onEditSetting(sender, args)
        } else if(ChatSession.isPrompted(sender)) {
            ChatSession.enter(sender, args.slice(1 until args.size).joinToString(" ").trim())
        } else {
            sender.sendMessage(emComponent("<prefix><error>You are not in an editing session."))
        }

        if(args.size == 1) {
            DefaultResponse.helpEnter(sender)
        }
    }

    fun onEditParameter(sender: Player, args: Array<String>) {
        if(args.size > 1){
            val sb = StringBuilder()

            for (loopArgs in 1 until args.size) {
                sb.append(args[loopArgs]).append(" ")
            }

            // Create val for the given value and remove the last char (because it's a space)
            val message = "$sb".dropLast(1)

            val showCategory = EditingPlayers.get(sender)!!.first.getCategory()
            val showName = EditingPlayers.get(sender)!!.first.getName()
            val effectShow = EffectShow(showCategory, showName)

            val id = EditingPlayers.get(sender)!!.second
            val parameter = EditingPlayers.get(sender)!!.third
            val effect = effectShow.getEffect(id)

            if(message.equals("cancel", ignoreCase = true)){
                sender.sendMessage(emComponent("<prefix><success>Cancelled edit."))
                val editEffectGui = EditEffectGui(sender, id, effectShow, 0)
                editEffectGui.open()
                EditingPlayers.remove(sender)
            }else{
                if(effect?.getDefaults()?.find { it.name == parameter.name }?.parameterValidator?.isValid(message) == true){
                    effectShow.getEffect(id)!!.getSection(effectShow, id).set(parameter.name, effect.getDefaults().find { it.name == parameter.name }?.parameterTypeConverter?.getAsType(message))
                    effectShow.reloadConfig()

                    sender.sendMessage(emComponent("<prefix><success>Edited parameter."))
                    val editEffectGui = EditEffectGui(sender, id, effectShow, 0)
                    editEffectGui.open()
                    EditingPlayers.remove(sender)
                }else{
                    sender.sendMessage(emComponent("<prefix><error>The value entered is not possible."))
                    sender.sendMessage(emComponent("<prefix><error>You need to enter a(n) $parameter, please read " +
                            "the info above."))
                    sender.sendMessage(emComponent("<prefix><error>Entered value: '$message'"))
                }
            }
        }
    }

    fun onEditSetting(player: Player, args: Array<String>) {
        if(args.size > 1){
            val sb = StringBuilder()

            for (loopArgs in 1 until args.size) {
                sb.append(args[loopArgs]).append(" ")
            }

            // Create val for the given value and remove the last char (because it's a space)
            val message = "$sb".dropLast(1)

            val showCategory = SettingsPlayers.get(player)!!.first.getCategory()
            val showName = SettingsPlayers.get(player)!!.first.getName()
            val effectShow = EffectShow(showCategory, showName)
            val setting = SettingsPlayers.get(player)!!.second

            if(message.equals("cancel", ignoreCase = true)){
                player.sendMessage(emComponent("<prefix><success>Cancelled edit."))
                val showSettingsGui = ShowSettingsGui(player, effectShow)
                showSettingsGui.open()
                SettingsPlayers.remove(player)
            }

            when(setting) {
                "looping-delay" -> {
                    if(message.toLongOrNull() == null) {
                        player.sendMessage(emComponent("<short_prefix><error>You're editing the looping delay. You must enter a number."))
                        return
                    }
                    effectShow.loopingDelay = message.toLong()
                    player.sendMessage(emComponent("<short_prefix><success>The delay has been set to $message ticks."))
                }

                "looping-interval" -> {
                    if(message.toLongOrNull() == null) {
                        player.sendMessage(emComponent("<short_prefix><error>You're editing the looping interval. You must enter a number."))
                        return
                    }
                    effectShow.loopingInterval = message.toLong()
                    player.sendMessage(emComponent("<short_prefix><success>The interval has been set to $message ticks."))
                }

                "center-location" -> {
                    if(LocationUtils.getLocationFromString(message) == null){
                        player.sendMessage(emComponent("<short_prefix><error>You're editing the center location. You must enter a valid location."))
                        return
                    }

                    effectShow.centerLocation = LocationUtils.getLocationFromString(message)
                    player.sendMessage(emComponent("<short_prefix><success>The center location has been set to $message."))
                }
            }
            val showSettingsGui = ShowSettingsGui(player, effectShow)
            showSettingsGui.open()
            SettingsPlayers.remove(player)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

}