package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.show.EditShowGui
import me.m64diamondstar.effectmaster.shows.utils.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.NumberFormatException

class EditorSubCommand: SubCommand {

    override fun getName(): String {
        return "editor"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size in 3..4) {
            if (!DefaultResponse.existsShow(sender, args))
                return
            if (sender !is Player) {
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You can only use this command as a player."))
                return
            }

            val effectShow = EffectShow(args[1], args[2], null)

            if(args.size == 3) {
                val editShowGui = EditShowGui(player = sender, effectShow)
                editShowGui.open()
            }else{
                try{
                    val editEffectGui = EditEffectGui(sender, args[3].toInt(), effectShow)
                    editEffectGui.open()
                }catch (e: NumberFormatException){
                    sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "'${args[3]}' is not a number."))
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
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.name) }

        if(args.size > 3){
            if(!ShowUtils.existsShow(args[1], args[2])){
                tabs.add("SHOW_DOES_NOT_EXIST")
                return tabs
            }
        }
        return tabs
    }

}