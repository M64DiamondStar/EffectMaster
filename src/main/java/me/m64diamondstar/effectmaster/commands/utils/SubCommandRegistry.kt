package me.m64diamondstar.effectmaster.commands.utils

import me.m64diamondstar.effectmaster.commands.subcommands.*

object SubCommandRegistry {

    /**
     * Register all SubCommands on start.
     */
    fun loadSubCommands(){
        SubCommandManager.registerSubCommand(CreateSubCommand())
        SubCommandManager.registerSubCommand(DeleteSubCommand())
        SubCommandManager.registerSubCommand(PlaySubCommand())
        SubCommandManager.registerSubCommand(PlayAtSubCommand())
        SubCommandManager.registerSubCommand(PlayCategorySubCommand())
        SubCommandManager.registerSubCommand(LocationSubCommand())
        SubCommandManager.registerSubCommand(WikiSubCommand())
        SubCommandManager.registerSubCommand(EditSubCommand())
        SubCommandManager.registerSubCommand(EditorSubCommand())
        SubCommandManager.registerSubCommand(RenameSubCommand())
        SubCommandManager.registerSubCommand(PrivatePlaySubCommand())
        SubCommandManager.registerSubCommand(CancelSubCommand())
        SubCommandManager.registerSubCommand(EnterSubCommand())
        SubCommandManager.registerSubCommand(ReloadSubCommand())
    }

}