package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import org.bukkit.command.CommandSender

class PlayEffectSubCommand: SubCommand {

    override fun getName(): String {
        return "playeffect"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {

    }

    override fun getTabCompleters(sender: CommandSender, args : Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        return tabs
    }

}