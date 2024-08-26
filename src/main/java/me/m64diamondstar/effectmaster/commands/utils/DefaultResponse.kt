package me.m64diamondstar.effectmaster.commands.utils

import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender

object DefaultResponse {

    fun help(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + ""))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Here is a list of all the possible sub-commands:"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString()))
        sender.sendMessage(Colors.format(Prefix.PrefixType.WHITE.toShortString() + " ✦ Show Playing ✦"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em play <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em play <category> <show> only <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em play <category> <show> from <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em playcategory <category>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em privateplay <category> <show> <selector>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + ""))
        sender.sendMessage(Colors.format(Prefix.PrefixType.WHITE.toShortString() + " ✦ Show Management ✦"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em create <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em delete <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em rename <category> <show> <new name>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em editor <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + ""))
        sender.sendMessage(Colors.format(Prefix.PrefixType.WHITE.toShortString() + " ✦ Effect Editing ✦"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em edit <category> <show> ..."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em location"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em enter <arguments>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em cancel"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + ""))
    }

    fun existsShow(sender: CommandSender, args: Array<String>): Boolean{

        if(!ShowUtils.existsCategory(args[1])){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The category &o${args[1]}&r " +
                    "${Colors.Color.ERROR}doesn't exist!"))
            return false
        }

        if(!ShowUtils.existsShow(args[1], args[2])){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The show &o${args[2]}&r " +
                    "${Colors.Color.ERROR}doesn't exist!"))
            return false
        }

        return true
    }

    fun helpCreate(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em create <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Creates a new show."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpDelete(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em delete <category> [show]"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Deletes a show."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpPlay(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em play <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em play <category> <show> only <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em play <category> <show> from <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Starts a show."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpPrivatePlay(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em privateplay <category> <show> <selector>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Only plays a show for a selection of players."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpRename(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em rename <category> <show> <new name>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Renames a show."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpEdit(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em edit <category> <show> edit <effect id> <parameter> <value>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em edit <category> <show> create <effect type>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em edit <category> <show> delete <effect id>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Edit, add or remove effects to/from a show."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpEditor(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em editor <category> <show> [id]"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Opens the show editor."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpCancel(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em cancel"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Cancels the the effect editing. (Can be used when something" +
                " prevents you from typing in chat."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpEnter(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em enter <parameter>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Enters a parameter field. (Can be used when something" +
                " prevents you from typing in chat."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun helpPlayCategory(sender: CommandSender) {
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "/em playcategory <category>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Plays all shows in a category."))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

    fun sendWiki(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "Here's the wiki for more information:"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "https://effectmaster.m64.dev/"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

}