package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import org.bukkit.command.CommandSender

class WikiSubCommand: SubCommand {

    override fun getName(): String {
        return "wiki"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        DefaultResponse.sendWiki(sender)
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

}