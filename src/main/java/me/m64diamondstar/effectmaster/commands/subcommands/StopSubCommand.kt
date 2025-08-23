package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

class StopSubCommand: SubCommand {

    override fun getName(): String {
        return "stop"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size == 1){
            sender.sendMessage(emComponent("<prefix><error>Please select which shows you want to cancel."))
            return
        }

        when(args[1]){

            "all" -> {
                ShowUtils.getRunningShows().forEach { it.cancel() }
                sender.sendMessage(emComponent("<prefix><success>All show instances have been cancelled."))
            }

            "category" -> {
                if(args.size != 3){
                    sender.sendMessage(emComponent("<prefix><error>Please use '/em stop category <category>'."))
                    return
                }
                if(!ShowUtils.existsCategory(args[2])){
                    sender.sendMessage(emComponent("<prefix><error>The category '${args[2]}' does not exist."))
                    return
                }
                ShowUtils.getRunningShows(args[2]).forEach { it.cancel() }
                sender.sendMessage(emComponent("<prefix><success>All show instances in the category '${args[2]}' have been cancelled."))
            }

            "show" -> {
                if(args.size != 4){
                    sender.sendMessage(emComponent("<prefix><error>Please use '/em stop show <category> <name>'."))
                    return
                }
                if(!ShowUtils.existsCategory(args[2])){
                    sender.sendMessage(emComponent("<prefix><error>The category '${args[2]}' does not exist."))
                    return
                }
                if(!ShowUtils.existsShow(args[2], args[3].replace(".yml", ""))){
                    sender.sendMessage(emComponent("<prefix><error>The show '${args[3].replace(".yml", "")}' in category '${args[2]}' does not exist."))
                    return
                }
                ShowUtils.getRunningShows(args[2], args[3].replace(".yml", "")).forEach { it.cancel() }
                sender.sendMessage(emComponent("<prefix><success>All show instances in the category '${args[2]}' and name '${args[3].replace(".yml", "")}' have been cancelled."))
            }

        }

    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2){
            tabs.addAll(listOf("all", "category", "show"))
        }
        else if(args.size == 3 && (args[1].equals("category", ignoreCase = true) || args[1].equals("show", ignoreCase = true))){
            ShowUtils.getCategories().forEach { tabs.add(it.nameWithoutExtension) }
        }
        else if(args.size == 4 && args[1].equals("show", ignoreCase = true)){
            ShowUtils.getShows(args[2]).forEach { tabs.add(it.nameWithoutExtension) }
        }

        return tabs
    }

}