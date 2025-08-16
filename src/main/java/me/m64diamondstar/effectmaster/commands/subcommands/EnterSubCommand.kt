package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EnterSubCommand: SubCommand {

    override fun getName(): String {
        return "enter"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You can only use this command as a player."))
            return
        }

        if(!EditingPlayers.contains(player = sender)){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You are not editing a parameter."))
            return
        }

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
                sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Cancelled edit."))
                val editEffectGui = EditEffectGui(sender, id, effectShow, 0)
                editEffectGui.open()
                EditingPlayers.remove(sender)
            }else{
                if(effect?.getDefaults()?.find { it.name == parameter.name }?.parameterValidator?.isValid(message) == true){
                    effectShow.getEffect(id)!!.getSection(effectShow, id).set(parameter.name, effect.getDefaults().find { it.name == parameter.name }?.parameterTypeConverter?.getAsType(message))
                    effectShow.reloadConfig()

                    sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Edited parameter."))
                    val editEffectGui = EditEffectGui(sender, id, effectShow, 0)
                    editEffectGui.open()
                    EditingPlayers.remove(sender)
                }else{
                    sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The value entered is not possible."))
                    sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You need to enter a(n) $parameter, please read " +
                            "the info above."))
                    sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "Entered value: '$message'"))
                }
            }
        }
        else {
            DefaultResponse.helpEnter(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

}