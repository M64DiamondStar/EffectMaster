package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.wand.Wand
import me.m64diamondstar.effectmaster.editor.wand.WandRegistry
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WandSubCommand: SubCommand {

    override fun getName(): String {
        return "wand"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        val target = if(args.size == 3) {
            val player = Bukkit.getPlayer(args[2])
            if (player == null) {
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The player '${args[2]}' is not online."))
                return
            }
            player
        }else if(args.size == 2) {
            if (sender !is Player) {
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You aren't a player!"))
                return
            }
            sender
        }else return

        val wand = Wand.getWand(args[1])
        if(wand == null){
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The wand '${args[1]}' could not be found."))
            return
        }

        wand.givePlayer(target)
        sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + wand.displayName
                + Colors.Color.SUCCESS.toString() + " has been delivered."))
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