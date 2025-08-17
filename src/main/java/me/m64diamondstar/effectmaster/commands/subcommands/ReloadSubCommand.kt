package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender

class ReloadSubCommand: SubCommand {

    override fun getName(): String {
        return "reload"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        EffectMaster.plugin().reloadConfig()
        ShowUtils.getCategories().forEach { categoryFile ->
            ShowUtils.getShows(categoryFile.name).forEach { showFile ->
                val effectShow = EffectShow(categoryFile.name, showFile.nameWithoutExtension)
                effectShow.reloadConfig()
                ShowLooper.updateLoop(effectShow)
            }
        }
        sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully reloaded the plugin."))
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }
}