package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

class DeleteSubCommand: SubCommand {
    override fun getName(): String {
        return "delete"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size == 3) {
            // Check if show already exists
            if (ShowUtils.existsCategory(args[1])) {
                if (ShowUtils.existsShow(args[1], args[2])) {
                    val effectShow = EffectShow(args[1], args[2])
                    effectShow.deleteShow()
                    sender.sendMessage(emComponent("<prefix><error>Successfully deleted the show ${args[2]} in category ${args[1]}."))
                    return
                }
            }

            // Not found
            sender.sendMessage(emComponent("<prefix><error>This show doesn't exist."))

        }
        else if(args.size == 2) {
            // Check if show already exists
            if (ShowUtils.existsCategory(args[1])) {
                if(ShowUtils.getCategory(args[1]).deleteRecursively())
                    sender.sendMessage(emComponent("<prefix><success>Successfully deleted the category ${args[1]}."))
                else
                    sender.sendMessage(emComponent("<prefix><error>Something went wrong. Please try again later."))

                return
            }

            sender.sendMessage(emComponent("<prefix><error>This category doesn't exist."))
        }
        // Sender entered command wrongly
        else {
            DefaultResponse.helpDelete(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        if(args.size == 3)
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.nameWithoutExtension) }
        return tabs
    }
}