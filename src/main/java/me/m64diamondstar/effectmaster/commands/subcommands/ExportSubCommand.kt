package me.m64diamondstar.effectmaster.commands.subcommands

import kotlinx.coroutines.launch
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.PluginScope
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.request.PasteServer
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender

class ExportSubCommand: SubCommand {

    // /em export preset <type> <name>
    // /em export show <category> <name>
    override fun getName(): String {
        return "export"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size >= 4) {
            when(args[1]) {
                "preset" -> exportPreset(sender, args)
                "show" -> exportShow(sender, args)
            }
        }else{
            DefaultResponse.helpExport(sender)
        }
    }

    private fun exportShow(sender: CommandSender, args: Array<String>) {
        if(!DefaultResponse.existsShow(sender, args, 2)) // Checks whether the show exists
            return

        val effectShow = EffectShow(args[2], args[3])

        val title = if (args.size > 4) args.copyOfRange(4, args.size).joinToString(" ") else null

        PluginScope.launch {
            val content = effectShow.rawConfig()
            exportContent(
                sender = sender,
                content = content,
                title = title,
                type = "show")
        }
    }

    private fun exportPreset(sender: CommandSender, args: Array<String>) {
        val title = if (args.size > 4) args.copyOfRange(4, args.size).joinToString(" ") else null

        PluginScope.launch {
            val content = EffectMaster.getEffectPresets().rawConfig(args[2], args[3])
            if(content == null) { // Check if preset exists
                sender.sendMessage(emComponent("<prefix><error>The preset ${args[3]} of type ${args[2].lowercase()} does not exist."))
                return@launch
            }

            exportContent(
                sender = sender,
                content = content,
                title = title,
                type = "preset")
        }
    }

    private suspend fun exportContent(sender: CommandSender, content: String, maxLength: Int = 100000, title: String?, type: String) {
        if(content.length > maxLength) { // Don't allow shows with more than 100,000 characters.
            sender.sendMessage(emComponent("<prefix><error>Your configuration file is too big to be uploaded."))
            return
        }

        sender.sendMessage(emComponent("<background>Generating link..."))

        val response = PasteServer.createPaste( // Sends API POST request to paste server
            content = content,
            title = title
        )

        val url = "https://paste.m64.dev/paste/view/$response?lang=yml"
        sender.sendMessage(emComponent("<prefix><default>View your $type " +
                "<u><click:open_url:'$url'><hover:show_text:'$url'>here</hover></click></u>."))
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            tabs.addAll(listOf("preset", "show"))

        if(args.size > 2)
            when(args[1].lowercase()) {
                "preset" -> {
                    if(args.size == 3) {
                        tabs.addAll(Effect.Type.getAllEffects().map { it.getIdentifier() })
                    }

                    else if(args.size == 4) {
                        tabs.addAll(EffectMaster.getEffectPresets().getAllPresets(args[2]).map { it.name })
                    }
                }

                "show" -> {
                    if(args.size == 3) {
                        tabs.addAll(ShowUtils.getCategories().map { it.nameWithoutExtension })
                    }

                    else if (args.size == 4) {
                        tabs.addAll(ShowUtils.getShows(args[2]).map { it.nameWithoutExtension })
                    }
                }
            }
        return tabs
    }

}