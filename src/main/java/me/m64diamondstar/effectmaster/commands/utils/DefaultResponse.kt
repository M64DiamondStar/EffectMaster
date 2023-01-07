package me.m64diamondstar.effectmaster.commands.utils

import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender

object DefaultResponse {

    fun help(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "Here is a list of all the possible sub-commands:"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString()))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em play <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em play <category> <show> only <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em play <category> <show> from <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em create <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em delete <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString()))
        sendWiki(sender)
    }

    fun existsShow(sender: CommandSender, args: Array<String>): Boolean{

        if(!ShowUtils.existsCategory(args[1])){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The category &o${args[1]}&r " +
                    "${Prefix.PrefixType.ERROR}doesn't exist!"))
            return false
        }

        if(!ShowUtils.existsShow(args[1], args[2])){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The show &o${args[2]}&r " +
                    "${Prefix.PrefixType.ERROR}doesn't exist!"))
            return false
        }

        return true
    }

    fun helpCreate(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em create <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + ""))
        sendWiki(sender)
    }

    fun helpDelete(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em delete <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + ""))
        sendWiki(sender)
    }

    fun helpPlay(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em play <category> <show>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em play <category> <show> only <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "/em play <category> <show> from <effect index>"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + ""))
        sendWiki(sender)
    }

    fun sendWiki(sender: CommandSender){
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "Here's the wiki for more information:"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() + "https://github.com/M64DiamondStar/EffectMaster/wiki/Command"))
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))
    }

}