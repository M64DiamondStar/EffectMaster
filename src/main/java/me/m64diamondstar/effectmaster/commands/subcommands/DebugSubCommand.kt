package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.utils.ShowErrorHandler
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugSubCommand: SubCommand {
    override fun getName(): String {
        return "debug"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {

        if (args.size < 2) {
            if (sender !is Player) {
                sender.sendMessage(emComponent("<prefix><error>Only players can use this command."))
                return
            }

            if (ShowErrorHandler.isDebugPlayer(sender)) {
                ShowErrorHandler.removeDebugPlayer(sender)
                sender.sendMessage(emComponent("<prefix><error>You will no longer receive error reports in chat"))
            } else {
                ShowErrorHandler.addDebugPlayer(sender)
                sender.sendMessage(emComponent("<prefix><success>You will now receive error reports in chat."))
            }

            return
        }

        when (args[1].lowercase()) {

            "on" -> {
                if (sender !is Player) {
                    sender.sendMessage(emComponent("<prefix><error>Only players can use this command."))
                    return
                }

                ShowErrorHandler.addDebugPlayer(sender)
                sender.sendMessage(emComponent("<prefix><success>You will now receive error reports in chat."))
            }

            "off" -> {
                if (sender !is Player) {
                    sender.sendMessage(emComponent("<prefix><error>Only players can use this command."))
                    return
                }

                ShowErrorHandler.removeDebugPlayer(sender)
                sender.sendMessage(emComponent("<prefix><success>You will no longer receive error reports in chat"))
            }

            "list" -> {
                val errors = ShowErrorHandler.list()
                if (errors.isEmpty()) {
                    sender.sendMessage(emComponent("<prefix><success>No errors have been reported yet."))
                } else {
                    sender.sendMessage(emComponent("<prefix><error>Currently reported errors:"))
                    errors.forEach { errorKey ->
                        sender.sendMessage(emComponent(" ⋅ <background>$errorKey"))
                    }
                }
            }

            "clear" -> {
                ShowErrorHandler.clear()
                sender.sendMessage(emComponent("<prefix><success>Cleared all reported errors."))
            }

             else -> DefaultResponse.helpDebug(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()

        if (args.size == 2) {
            tabs.addAll(listOf("on", "off", "list", "clear"))
        }

        return tabs
    }
}