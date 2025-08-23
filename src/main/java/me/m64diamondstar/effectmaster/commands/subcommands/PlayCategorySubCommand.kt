package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.command.CommandSender

class PlayCategorySubCommand: SubCommand {

    override fun getName(): String {
        return "playcategory"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {

        if(args.size != 2){
            DefaultResponse.helpPlayCategory(sender)
            return
        }
        if(!ShowUtils.existsCategory(args[1])) {
            sender.sendMessage(
                emComponent(
                    "<prefix><error>The category &o${args[1]}&r " +
                            "<error>doesn't exist!"
                )
            )
            return
        }

        ShowUtils.getShows(args[1]).forEach {
            val effectShow = EffectShow(args[1], it.name)
            effectShow.play(null)
        }

    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        return tabs
    }

}