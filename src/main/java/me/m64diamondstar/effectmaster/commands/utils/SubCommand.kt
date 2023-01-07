package me.m64diamondstar.effectmaster.commands.utils

import org.bukkit.command.CommandSender

interface SubCommand {

    /**
     * @return the name of this subcommand
     */
    fun getName(): String

    /**
     * This method will be called when the subcommand is executed
     */
    fun execute(sender: CommandSender, args: Array<String>)

    /**
     * Adds all the tab completers needed in this sub-command
     * @return the list of the current needed tab completions
     */
    fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String>

}