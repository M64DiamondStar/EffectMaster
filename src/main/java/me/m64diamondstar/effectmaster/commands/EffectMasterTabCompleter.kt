package me.m64diamondstar.effectmaster.commands

import me.m64diamondstar.effectmaster.commands.utils.SubCommandManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EffectMasterTabCompleter : TabCompleter {

    private val tc = ArrayList<String>()

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<String>): MutableList<String> {

        if(args.size == 1){
            tc.clear()
            SubCommandManager.getAllSubCommands().forEach { tc.add(it.getName()) }
            tc.add("help")
        }else if(args.size > 1){
            tc.clear()
            if(SubCommandManager.fromString(args[0]) != null)
                SubCommandManager.fromString(args[0])?.getTabCompleters(sender = sender, args = args)!!.forEach { tc.add(it) }
        }
        else
            tc.clear()

        val result = ArrayList<String>()
        for(a in tc){
            if(a.lowercase().startsWith(args[args.size - 1].lowercase()))
                result.add(a)
        }

        return result
    }

}