package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender

class VersionSubCommand: SubCommand {

    override fun getName(): String {
        return "version"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        (sender as Audience).sendMessage(emComponent("<prefix><default>The server is running version ${EffectMaster.plugin().pluginMeta.version}"))
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

}