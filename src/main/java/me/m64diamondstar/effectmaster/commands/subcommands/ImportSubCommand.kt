package me.m64diamondstar.effectmaster.commands.subcommands

import kotlinx.coroutines.launch
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.utils.ChatSession
import me.m64diamondstar.effectmaster.ktx.PluginScope
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.request.PasteServer
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ImportSubCommand: SubCommand {

    override fun getName(): String {
        return "import"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) { // Because of chat interaction, you need to be a player to execute this command
            sender.sendMessage(emComponent("<prefix><error>You aren't a player!"))
            return
        }

        if(args.size == 3) {
            val url = args[2]

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
                if(response.isNullOrBlank() || response.startsWith("{\"error\":")) {
                    sender.sendMessage(emComponent("<prefix><error>Failed to retrieve the paste. " +
                            "Please check that the URL is correct and that the paste is public. " +
                            "If the problem persists, contact the developer."))
                    return@launch
                }

                // Start prompts
                when(args[1]) {
                    "preset" -> importPreset(sender, response)
                    "show" -> importShow(sender, response)
                }
            }
        }else{
            DefaultResponse.helpImport(sender)
        }
    }

    /**
     * Import a show through chat interaction
     * @param sender the player to start the chat interaction with
     * @param response the data to import
     */
    fun importShow(sender: Player, response: String) {

        fun namePrompt(category: String){
            ChatSession.prompt(
                player = sender,
                prompt = emComponent("<prefix><default>Please enter a show name for this import:"),
                validator = { it.matches(Regex("^[a-zA-Z0-9_-]+$")) && !ShowUtils.existsShow(category, it) },
                onInvalid = {
                    if(ShowUtils.existsShow(category, it))
                        sender.sendMessage(emComponent("<prefix><error>This show already exists. Please use another name:"))
                    else
                        sender.sendMessage(emComponent("<prefix><error>Please use letters, numbers, _ and - only:"))
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
                prompt = emComponent("<prefix><default>Please enter a category for this import:"),
                validator = { it.matches(Regex("^[a-zA-Z0-9_-]+$")) },
                onInvalid = { sender.sendMessage(emComponent("<prefix><error>Please use letters, numbers, _ and - only:")) },
                onComplete = {
                    namePrompt(it)
                },
            )
        }

        categoryPrompt()
    }

    /**
     * Import a show through chat interaction
     * @param sender the player to start the chat interaction with
     * @param response the data to import
     */
    fun importPreset(sender: Player, response: String) {
        // The preset type
        val type = response.split(":\n")[0].uppercase()

        fun namePrompt(material: String) {
            ChatSession.prompt(
                player = sender,
                prompt = emComponent("<prefix><default>Please enter a <b>name</b> for this import:"),
                validator = { EffectMaster.getEffectPresets().getPreset(type, it) == null && it.matches(Regex("^[a-zA-Z0-9_-]+$")) },
                onInvalid = {
                    if(EffectMaster.getEffectPresets().getPreset(type, it) != null)
                        sender.sendMessage(emComponent("<prefix><error>This preset name already exists. Please choose another:"))
                    else
                        sender.sendMessage(emComponent("<prefix><error>Please use letters, numbers, _ and - only:"))
                            },
                onComplete = { name ->
                    EffectMaster.getEffectPresets().import(response, name, material)
                    sender.sendMessage(emComponent("<prefix><success>The preset $name has been created for the type $type!"))
                }
            )
        }

        fun materialPrompt() {
            ChatSession.prompt(
                player = sender,
                prompt = emComponent("<prefix><default>Please enter a <b>display material</b> for this import:<br><background>Example: 'DIAMOND_SWORD'"),
                validator = { material -> Material.entries.map { it.name }.contains(material.uppercase()) },
                onInvalid = { sender.sendMessage(emComponent("<prefix><error>Please enter a valid material:")) },
                onComplete = { material ->
                    namePrompt(material.uppercase())
                }
            )
        }

        materialPrompt()
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()

        if(args.size == 2) {
            tabs.addAll(listOf("show", "preset"))
        }

        return tabs
    }

}