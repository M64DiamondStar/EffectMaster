package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.ShowLooper
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

class LoopSubCommand: SubCommand {

    override fun getName(): String {
        return "loop"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.size == 1) {
            DefaultResponse.helpLoop(sender)
            return
        }
        when(args[1]) {
            "list" -> {
                if (args.size != 2) {
                    DefaultResponse.helpLoop(sender)
                    return
                }
                val shows = ShowLooper.getLooping()
                if (shows.isEmpty()) {
                    sender.sendMessage(emComponent("<prefix>There're no active looping shows."))
                    return
                }
                sender.sendMessage(emComponent("<prefix>All the currently looping shows:"))
                ShowLooper.getLooping().forEach { (category, name) ->
                    sender.sendMessage(emComponent(
                        "<default>- <hover:show_text:'<default>Click to edit'>" +
                                "<click:run_command:'/em editor $category $name settings'>$category/$name" +
                                "</click></hover>")
                    )
                }
            }

            "enable" -> {
                if (args.size != 4) {
                    DefaultResponse.helpLoop(sender)
                    return
                }
                if (DefaultResponse.existsShow(sender, args, 2)) {
                    if(ShowLooper.isLooping(args[2], args[3])) {
                        sender.sendMessage(emComponent("<prefix><error>This show is already looping."))
                    }
                    val effectShow = EffectShow(args[2], args[3])
                    effectShow.looping = true
                    ShowLooper.loop(effectShow)
                    sender.sendMessage(emComponent("<prefix><success>Enabled looping for ${args[2]}/${args[3]}."))
                }
            }

            "disable" -> {
                if (args.size != 4) {
                    DefaultResponse.helpLoop(sender)
                    return
                }
                if (DefaultResponse.existsShow(sender, args, 2)) {
                    if(!ShowLooper.isLooping(args[2], args[3])) {
                        sender.sendMessage(emComponent("<prefix><error>This show isn't looping."))
                    }
                    val effectShow = EffectShow(args[2], args[3])
                    effectShow.looping = false
                    ShowLooper.stopLoop(effectShow)
                    sender.sendMessage(emComponent("<prefix><success>Disabled looping for ${args[2]}/${args[3]}."))
                }
            }

            "interval" -> {
                if (args.size != 5) {
                    DefaultResponse.helpLoop(sender)
                    return
                }
                if(args[4].toLongOrNull() == null) {
                    sender.sendMessage(emComponent("<prefix><error>Please enter a valid number as interval."))
                    return
                }
                if (DefaultResponse.existsShow(sender, args, 2)) {
                    val effectShow = EffectShow(args[2], args[3])
                    effectShow.loopingInterval = args[4].toLong()
                    sender.sendMessage(emComponent("<prefix><success>Changed the looping interval of ${args[2]}/${args[3]} to ${args[4]}."))
                }
            }

            "delay" -> {
                if (args.size != 5) {
                    DefaultResponse.helpLoop(sender)
                    return
                }
                if(args[4].toLongOrNull() == null) {
                    sender.sendMessage(emComponent("<prefix><error>Please enter a valid number as delay."))
                    return
                }
                if (DefaultResponse.existsShow(sender, args, 2)) {
                    val effectShow = EffectShow(args[2], args[3])
                    effectShow.loopingDelay = args[4].toLong()
                    sender.sendMessage(emComponent("<prefix><success>Changed the looping delay of ${args[2]}/${args[3]} to ${args[4]}."))
                }
            }

            else -> {
                DefaultResponse.helpLoop(sender)
            }
        }
    }

    override fun getTabCompleters(
        sender: CommandSender,
        args: Array<String>
    ): ArrayList<String> {
        val tabs = arrayListOf<String>()

        when(args.size) {
            2 -> {
                tabs.addAll(listOf("list", "enable", "disable", "interval", "delay"))
            }

            3 -> {
                if(args[1].lowercase() in listOf("enable", "disable", "interval", "delay")) {
                    ShowUtils.getCategories().forEach { tabs.add(it.name) }
                }
            }
            4 -> {
                if(args[1].lowercase() in listOf("enable", "disable", "interval", "delay")) {
                    ShowUtils.getShows(args[2]).forEach { tabs.add(it.nameWithoutExtension) }
                }
            }
        }

        return tabs
    }
}