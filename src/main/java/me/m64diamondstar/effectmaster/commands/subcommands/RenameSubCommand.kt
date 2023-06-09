package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RenameSubCommand: SubCommand {
    override fun getName(): String {
        return "rename"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size == 4) {
            if (!DefaultResponse.existsShow(sender, args))
                return
            if (sender !is Player) {
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You can only use this command as a player."))
                return
            }

            val effectShow = EffectShow(args[1], args[2], null)
            effectShow.rename(args[3])

            sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Renamed the show to ${args[3]}."))

        }else{
            DefaultResponse.helpRename(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        if(args.size == 3)
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.name) }

        if(args.size > 3){
            if(!ShowUtils.existsShow(args[1], args[2])){
                tabs.add("SHOW_DOES_NOT_EXIST")
                return tabs
            }else{
                tabs.add("new_name")
            }
        }
        return tabs
    }
}