package me.m64diamondstar.effectmaster.commands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommandManager
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class EffectMasterCommand: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if(args.isEmpty() || args.size == 1 && args[0].equals("help", ignoreCase = true)){
            DefaultResponse.help(sender)
            return false
        }

        if(SubCommandManager.fromString(args[0]) == null){
            DefaultResponse.help(sender)
            return false
        }

        if(!sender.hasPermission("effectmaster.command.${SubCommandManager.fromString(args[0])?.getName()}")){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "No permission."))
            return false
        }
        SubCommandManager.fromString(args[0])?.execute(sender = sender, args = args)

        return false
    }



}