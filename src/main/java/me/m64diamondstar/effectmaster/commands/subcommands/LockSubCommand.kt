package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender
import kotlin.io.nameWithoutExtension

class LockSubCommand: SubCommand {
    override fun getName(): String {
        return "lock"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.size != 3) {
            DefaultResponse.helpLock(sender)
            return
        }

        if (!DefaultResponse.existsShow(sender, args))
            return

        val effectShow = EffectShow(args[1], args[2])

        if (effectShow.locked) {
            sender.sendMessage(emComponent("<prefix><error>This show is already locked."))
            return
        }

        effectShow.locked = true

        sender.sendMessage(emComponent("<prefix><success>Successfully locked the show ${args[2]} in category ${args[1]}."))
    }

    override fun getTabCompleters(
        sender: CommandSender,
        args: Array<String>
    ): ArrayList<String> {
        val tabs = ArrayList<String>()

        if (args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        if (args.size == 3)
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.nameWithoutExtension) }

        return tabs
    }
}