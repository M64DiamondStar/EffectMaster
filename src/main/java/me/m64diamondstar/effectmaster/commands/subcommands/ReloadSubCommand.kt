package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender

class ReloadSubCommand: SubCommand {

    override fun getName(): String {
        return "reload"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        EffectMaster.plugin.reloadConfig()
        sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully reloaded the config.yml."))
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }
}