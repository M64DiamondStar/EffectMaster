package me.m64diamondstar.effectmaster.commands.utils

object SubCommandManager {

    private val subCommands = ArrayList<SubCommand>()

    fun registerSubCommand(subCommand: SubCommand){
        subCommands.add(subCommand)
    }

    fun fromString(string: String): SubCommand?{
        subCommands.forEach {
            if (it.getName().equals(string, ignoreCase = true))
                return it
        }
        return null
    }

    fun contains(string: String): Boolean{
        subCommands.forEach {
            if (it.getName().equals(string, ignoreCase = true))
                return true
        }
        return false
    }

    fun getAllSubCommands(): ArrayList<SubCommand>{
        return subCommands
    }

}