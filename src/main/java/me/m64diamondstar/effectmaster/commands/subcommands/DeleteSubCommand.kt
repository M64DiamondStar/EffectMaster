package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.shows.utils.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
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
                    val effectShow = EffectShow(args[1], args[2], null)
                    effectShow.deleteShow()
                    sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully deleted the show ${args[2]} in category ${args[1]}."))
                    return
                }
            }

            // Not found
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "This show doesn't exist."))

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
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.name) }
        return tabs
    }
}