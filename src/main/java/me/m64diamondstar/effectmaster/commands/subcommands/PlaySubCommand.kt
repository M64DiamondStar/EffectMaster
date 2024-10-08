package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender
import java.lang.NumberFormatException

class PlaySubCommand: SubCommand {
    override fun getName(): String {
        return "play"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size >= 3){
            if(!DefaultResponse.existsShow(sender, args))
                return


            if(args.size == 3){
                val effectShow = EffectShow(args[1], args[2])
                effectShow.play(null)
                sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully started this show."))
            }

            else if(args.size == 5){
                if(args[3].equals("only", ignoreCase = true)){
                    try {
                        val effectShow = EffectShow(args[1], args[2])
                        if(effectShow.playOnly(args[4].toInt(), null))
                            sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully played effect ${args[4]} of this show."))
                        else
                            sender.sendMessage(Colors.format("${Prefix.PrefixType.ERROR}&n${args[4]}&r ${Colors.Color.ERROR}is not a valid ID."))
                    }catch (_: NumberFormatException){
                        sender.sendMessage(Colors.format("${Prefix.PrefixType.ERROR}&n${args[4]}&r ${Colors.Color.ERROR}is not a valid number."))
                    }
                }else if(args[3].equals("from", ignoreCase = true)){
                    try {
                        val effectShow = EffectShow(args[1], args[2])
                        if(args[4].toInt() > effectShow.getMaxId()){
                            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "&n${args[4]}&r ${Colors.Color.ERROR}is not a valid ID."))
                            return
                        }
                        effectShow.playFrom(args[4].toInt(), null)
                        sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully started from effect ${args[4]} of this show."))
                    }catch (_: NumberFormatException){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "&n${args[4]}&r ${Colors.Color.ERROR}is not a valid number."))
                    }
                }else DefaultResponse.helpPlay(sender)
            }else DefaultResponse.helpPlay(sender)
        }else DefaultResponse.helpPlay(sender)
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
            }
        }

        if(args.size == 4){
            tabs.add("from")
            tabs.add("only")
        }

        if(args.size == 5){
            val effectShow = EffectShow(args[1], args[2])
            for (i in 1..effectShow.getMaxId()){
                tabs.add("$i")
            }
        }

        return tabs
    }
}