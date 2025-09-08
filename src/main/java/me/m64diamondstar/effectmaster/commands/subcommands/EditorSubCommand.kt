package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.ui.effect.CreateEffectGui
import me.m64diamondstar.effectmaster.editor.ui.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.ui.effect.PresetEffectGui
import me.m64diamondstar.effectmaster.editor.ui.show.AllEffectsGui
import me.m64diamondstar.effectmaster.editor.ui.show.EditShowGui
import me.m64diamondstar.effectmaster.editor.ui.show.ShowSettingsGui
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EditorSubCommand: SubCommand {

    override fun getName(): String {
        return "editor"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size in 3..5) {
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

                    "presets" -> {
                        if(args.size < 5) {
                            sender.sendMessage(emComponent("<prefix><error>You must enter an effect type to view the presets of that type."))
                            return
                        }
                        val effectType = Effect.Type.getEffect(args[4])
                        if(effectType == null) {
                            sender.sendMessage(emComponent("<prefix><error>The effect type <i>${args[4]}</i> does not exist."))
                            return
                        }

                        val presetEffectGui = PresetEffectGui(sender, effectShow, 0, effectType)
                        presetEffectGui.open()
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

        if(args.size == 4) {
            if(!ShowUtils.existsShow(args[1], args[2])){
                tabs.add("SHOW_DOES_NOT_EXIST")
            }
            tabs.add("settings")
            tabs.add("create")
            tabs.add("presets")
            tabs.add("all")
        }

        if(args.size == 5){
            if(args[3].equals("presets", ignoreCase = true)){
                tabs.addAll(Effect.Type.getAllEffects().map { it.getIdentifier() })
            }
        }
        return tabs
    }

}