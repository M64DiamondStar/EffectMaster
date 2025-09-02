package me.m64diamondstar.effectmaster.commands.subcommands

import kotlinx.coroutines.launch
import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.utils.ChatSession
import me.m64diamondstar.effectmaster.ktx.PluginScope
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.request.PasteServer
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ImportSubCommand: SubCommand {

    override fun getName(): String {
        return "import"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(emComponent("<prefix><error>You aren't a player!"))
            return
        }

        if(args.size == 2) {

            val url = args[1]

            // User does not enter valid URL
            if(!url.startsWith("https://paste.m64.dev/paste/view/")
                && !url.startsWith("https://paste.m64.dev/paste/raw/")) {
                sender.sendMessage(emComponent("<prefix><error>You must use a url from the official " +
                        "<u><click:open_url:'https://paste.m64.dev/'>" +
                        "<hover:show_text:'https://paste.m64.dev/'>paste website</hover>" +
                        "</click></u>."))
                return
            }

            PluginScope.launch {
                sender.sendMessage(emComponent("<background>Retrieving data..."))

                val id = url.substringAfterLast('/').substringBefore('?')
                val response = PasteServer.getPasteContent(id)

                // Cancel if the ID doesn't exist or the request failed
                if(response == null || response.isBlank()) {
                    sender.sendMessage(emComponent("<prefix><error>Failed to retrieve the paste. " +
                            "Please check that the URL is correct and that the paste is public. " +
                            "If the problem persists, contact the developer."))
                    return@launch
                }

                fun namePrompt(category: String){
                    ChatSession.prompt(
                        player = sender,
                        prompt = emComponent("<prefix><default>Please enter a show name for this import."),
                        validator = { !ShowUtils.existsShow(category, it) && it.matches(Regex("^[a-zA-Z0-9_-]+$")) },
                        onInvalid = {
                            if(ShowUtils.existsShow(category, it))
                                sender.sendMessage(emComponent("<prefix><error>This show already exists. Please use another name:"))
                            else
                                sender.sendMessage(emComponent("<prefix><error>Please use letters, numbers, _ or - only."))
                                    },
                        onComplete = { name ->
                            val effectShow = EffectShow(category, name)
                            effectShow.import(response)
                            sender.sendMessage(emComponent("<prefix><success>The show $name in the category " +
                                    "$category has been created!"))
                            sender.sendMessage(emComponent("<click:run_command:'/em editor $category $name'>" +
                                    "<background>Click here to open the show editor."))
                        },
                    )
                }

                fun categoryPrompt(){
                    ChatSession.prompt(
                        player = sender,
                        prompt = emComponent("<prefix><default>Please enter a category for this import."),
                        validator = { it.matches(Regex("^[a-zA-Z0-9_-]+$")) },
                        onInvalid = { sender.sendMessage(emComponent("<prefix><error>Please use letters, numbers, _ or - only.")) },
                        onComplete = {
                            namePrompt(it)
                        },
                    )
                }

                // Start prompts
                categoryPrompt()
            }

        }else{
            DefaultResponse.helpImport(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        return tabs
    }

}