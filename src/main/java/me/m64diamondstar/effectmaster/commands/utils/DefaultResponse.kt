package me.m64diamondstar.effectmaster.commands.utils

import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

object DefaultResponse {

    fun help(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix>"))
        sender.sendMessage(emComponent("<short_prefix><default>Here is a list of all the possible sub-commands:"))
        sender.sendMessage(emComponent("<short_prefix>"))
        sender.sendMessage(emComponent("<short_prefix> ✦ Show Playing ✦"))
        sender.sendMessage(emComponent("<short_prefix><default>/em play <category> <show>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em play <category> <show> only <effect index>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em play <category> <show> from <effect index>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em playcategory <category>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em privateplay <category> <show> <selector>"))
        sender.sendMessage(emComponent("<short_prefix><default>"))
        sender.sendMessage(emComponent("<short_prefix> ✦ Show Management ✦"))
        sender.sendMessage(emComponent("<short_prefix><default>/em create <category> <show>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em delete <category> <show>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em rename <category> <show> <new name>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em editor <category> <show>"))
        sender.sendMessage(emComponent("<short_prefix><default>"))
        sender.sendMessage(emComponent("<short_prefix> ✦ Effect Editing ✦"))
        sender.sendMessage(emComponent("<short_prefix><default>/em edit <category> <show> ..."))
        sender.sendMessage(emComponent("<short_prefix><default>/em location"))
        sender.sendMessage(emComponent("<short_prefix><default>/em enter <arguments>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em cancel"))
        sender.sendMessage(emComponent("<short_prefix>"))
    }

    fun existsShow(sender: CommandSender, args: Array<String>): Boolean{

        if(!ShowUtils.existsCategory(args[1])){
            sender.sendMessage(emComponent("<prefix><error>The category &o${args[1]}&r " +
                    "<error>doesn't exist!"))
            return false
        }

        if(!ShowUtils.existsShow(args[1], args[2])){
            sender.sendMessage(emComponent("<prefix><error>The show &o${args[2]}&r " +
                    "<error>doesn't exist!"))
            return false
        }

        return true
    }

    fun helpCreate(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em create <category> <show>"))
        sender.sendMessage(emComponent("<short_prefix><default>Creates a new show."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpDelete(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em delete <category> [show]"))
        sender.sendMessage(emComponent("<short_prefix><default>Deletes a show."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpPlay(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em play <category> <show>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em play <category> <show> only <effect index>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em play <category> <show> from <effect index>"))
        sender.sendMessage(emComponent("<short_prefix><default>Starts a show."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpPrivatePlay(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em privateplay <category> <show> <selector>"))
        sender.sendMessage(emComponent("<short_prefix><default>Only plays a show for a selection of players."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpRename(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em rename <category> <show> <new name>"))
        sender.sendMessage(emComponent("<short_prefix><default>Renames a show."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpEdit(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em edit <category> <show> edit <effect id> <parameter> <value>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em edit <category> <show> create <effect type>"))
        sender.sendMessage(emComponent("<short_prefix><default>/em edit <category> <show> delete <effect id>"))
        sender.sendMessage(emComponent("<short_prefix><default>Edit, add or remove effects to/from a show."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpEditor(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em editor <category> <show> [id]"))
        sender.sendMessage(emComponent("<short_prefix><default>Opens the show editor."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpCancel(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em cancel"))
        sender.sendMessage(emComponent("<short_prefix><default>Cancels the the effect editing. (Can be used when something" +
                " prevents you from typing in chat."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpEnter(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em enter <parameter>"))
        sender.sendMessage(emComponent("<short_prefix><default>Enters a parameter field. (Can be used when something" +
                " prevents you from typing in chat."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun helpPlayCategory(sender: CommandSender) {
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>/em playcategory <category>"))
        sender.sendMessage(emComponent("<short_prefix><default>Plays all shows in a category."))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

    fun sendWiki(sender: CommandSender){
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
        sender.sendMessage(emComponent("<short_prefix><default>Here's the wiki for more information:"))
        sender.sendMessage(emComponent("<short_prefix><default>https://effectmaster.m64.dev/"))
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))
    }

}