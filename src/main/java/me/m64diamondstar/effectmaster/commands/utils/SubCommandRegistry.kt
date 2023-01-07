package me.m64diamondstar.effectmaster.commands.utils

import me.m64diamondstar.effectmaster.commands.subcommands.CreateSubCommand
import me.m64diamondstar.effectmaster.commands.subcommands.DeleteSubCommand
import me.m64diamondstar.effectmaster.commands.subcommands.LocationSubCommand
import me.m64diamondstar.effectmaster.commands.subcommands.PlaySubCommand

object SubCommandRegistry {

    /**
     * Register all SubCommands on start.
     */
    fun loadSubCommands(){
        SubCommandManager.registerSubCommand(CreateSubCommand())
        SubCommandManager.registerSubCommand(DeleteSubCommand())
        SubCommandManager.registerSubCommand(PlaySubCommand())
        SubCommandManager.registerSubCommand(LocationSubCommand())
    }

}