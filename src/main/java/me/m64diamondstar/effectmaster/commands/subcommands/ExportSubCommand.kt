package me.m64diamondstar.effectmaster.commands.subcommands

import kotlinx.coroutines.launch
import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.PluginScope
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.request.PasteServer
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

class ExportSubCommand: SubCommand {

    override fun getName(): String {
        return "export"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size >= 3) {
            if(!DefaultResponse.existsShow(sender, args)) // Checks whether the show exists
                return

            val effectShow = EffectShow(args[1], args[2])

            val title = if (args.size > 3) args.copyOfRange(3, args.size).joinToString(" ") else null

            PluginScope.launch {
                val content = effectShow.rawConfig()
                if(content.length > 100000) { // Don't allow shows with more than 100,000 characters.
                    sender.sendMessage(emComponent("<prefix><error>Your configuration file is too big to be uploaded."))
                    return@launch
                }

                sender.sendMessage(emComponent("<background>Generating link..."))

                val response = PasteServer.createPaste( // Sends API POST request to paste server
                    content = content,
                    title = title
                )
                val url = "https://paste.m64.dev/paste/view/$response?lang=yml"
                sender.sendMessage(emComponent("<prefix><default>View your effect " +
                        "<u><click:open_url:'$url'><hover:show_text:'$url'>here</hover></click></u>."))
            }

        }else{
            DefaultResponse.helpExport(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        if(args.size == 3)
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.nameWithoutExtension) }
        return tabs
    }

}