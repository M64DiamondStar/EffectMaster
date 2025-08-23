package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.effect.CreateEffectGui
import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.show.AllEffectsGui
import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.show.ShowSettingsGui
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EditorSubCommand: SubCommand {

    override fun getName(): String {
        return "editor"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size in 3..4) {
            if (!DefaultResponse.existsShow(sender, args))
                return
            if (sender !is Player) {
                sender.sendMessage(emComponent("<prefix><error>You can only use this command as a player."))
                return
            }

            val effectShow = EffectShow(args[1], args[2])

            if(args.size == 3) {
                val editShowGui = EditShowGui(player = sender, effectShow)
                editShowGui.open()
            } else {
                when(args[3].lowercase()) {
                    "settings" -> {
                        val editSettingsGui = ShowSettingsGui(sender, effectShow)
                        editSettingsGui.open()
                    }

                    // Opens create new effect menu
                    "create" -> {
                        val createEffectGui = CreateEffectGui(sender, effectShow, 0)
                        createEffectGui.open()
                    }

                    "all" -> {
                        val allEffectsGui = AllEffectsGui(sender, effectShow, 0)
                        allEffectsGui.open()
                    }

                    // Arg must be the effect ID the user wants to edit
                    else -> {
                        if(args[3].toIntOrNull() == null){
                            sender.sendMessage(emComponent("<prefix><error>'${args[3]}' is not a number."))
                            return
                        }
                        val editEffectGui = EditEffectGui(sender, args[3].toInt(), effectShow, 0)
                        editEffectGui.open()
                    }

                }
            }

        }else{
            DefaultResponse.helpEditor(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        if(args.size == 3)
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.nameWithoutExtension) }

        if(args.size > 3) {
            if(!ShowUtils.existsShow(args[1], args[2])){
                tabs.add("SHOW_DOES_NOT_EXIST")
            }
            tabs.add("settings")
            tabs.add("create")
            tabs.add("all")
        }
        return tabs
    }

}