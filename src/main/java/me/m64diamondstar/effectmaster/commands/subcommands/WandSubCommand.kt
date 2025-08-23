package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.wand.Wand
import me.m64diamondstar.effectmaster.editor.wand.WandRegistry
import me.m64diamondstar.effectmaster.ktx.emComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WandSubCommand: SubCommand {

    override fun getName(): String {
        return "wand"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        val target = when (args.size) {
            3 -> {
                val player = Bukkit.getPlayer(args[2])
                if (player == null) {
                    sender.sendMessage(emComponent("<prefix><error>The player '${args[2]}' is not online."))
                    return
                }
                player
            }
            2 -> {
                if (sender !is Player) {
                    sender.sendMessage(emComponent("<prefix><error>You aren't a player!"))
                    return
                }
                sender
            }
            else -> return
        }

        val wand = Wand.getWand(args[1])
        if(wand == null){
            sender.sendMessage(emComponent("<prefix><error>The wand '${args[1]}' could not be found."))
            return
        }

        wand.givePlayer(target)
        sender.sendMessage(emComponent("<prefix><success>").append(wand.displayName).append(emComponent(" <success>has been delivered.")))
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()

        if(args.size == 2)
            tabs.addAll(WandRegistry.getRegisteredWands().map { it.id })

        if(args.size == 3)
            tabs.addAll(Bukkit.getOnlinePlayers().map { it.name })

        return tabs
    }

}