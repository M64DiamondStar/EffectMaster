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

class CancelSubCommand: SubCommand {

    override fun getName(): String {
        return "cancel"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You can only use this command as a player."))
            return
        }

        if(args.size == 1){
            if(!EditingPlayers.contains(sender)) {
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You aren't editing an effect."))
                return
            }

            val showCategory = EditingPlayers.get(sender)!!.first.getCategory()
            val showName = EditingPlayers.get(sender)!!.first.getName()
            val effectShow = EffectShow(showCategory, showName)

            val id = EditingPlayers.get(sender)!!.second

            sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Cancelled edit."))
            val editEffectGui = EditEffectGui(sender, id, effectShow, 0)
            editEffectGui.open()
            EditingPlayers.remove(sender)
        }
        else {
            DefaultResponse.helpCancel(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

}