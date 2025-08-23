package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

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
                sender.sendMessage(emComponent("<prefix><success>Successfully started this show."))
            }

            else if(args.size == 5){
                if(args[3].equals("only", ignoreCase = true)){
                    try {
                        val effectShow = EffectShow(args[1], args[2])
                        if(effectShow.playOnly(args[4].toInt(), null))
                            sender.sendMessage(emComponent("<prefix><success>Successfully played effect ${args[4]} of this show."))
                        else
                            sender.sendMessage(emComponent("<error><italic>${args[4]}</italic> <error>is not a valid ID."))
                    }catch (_: NumberFormatException){
                        sender.sendMessage(emComponent("<error><italic>${args[4]}</italic> <error>is not a valid number."))
                    }
                }else if(args[3].equals("from", ignoreCase = true)){
                    try {
                        val effectShow = EffectShow(args[1], args[2])
                        if(args[4].toInt() > effectShow.getMaxId()){
                            sender.sendMessage(emComponent("<prefix><error><italic>${args[4]}</italic> <error>is not a valid ID."))
                            return
                        }
                        effectShow.playFrom(args[4].toInt(), null)
                        sender.sendMessage(emComponent("<prefix><success>Successfully started from effect ${args[4]} of this show."))
                    }catch (_: NumberFormatException){
                        sender.sendMessage(emComponent("<prefix><error><italic>${args[4]}</italic> <error>is not a valid number."))
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
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.nameWithoutExtension) }

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