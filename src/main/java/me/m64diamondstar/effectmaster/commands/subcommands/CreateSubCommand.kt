package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

class CreateSubCommand: SubCommand {
    override fun getName(): String {
        return "create"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size == 3) {
            // Check if show already exists
            if (ShowUtils.existsCategory(args[1])) {
                if (ShowUtils.existsShow(args[1], args[2])) {
                    sender.sendMessage(emComponent("<prefix><error>This show already exists. Please delete it if you want to replace it."))
                    return
                }
            }

            // Checks succeeded
            val effectShow = EffectShow(args[1], args[2])
            effectShow.createShow()
            sender.sendMessage(emComponent("<prefix><success>Successfully created the show ${args[2]} in category ${args[1]}."))

        }
        // Sender entered command wrongly
        else {
            DefaultResponse.helpCreate(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }
        return tabs
    }
}