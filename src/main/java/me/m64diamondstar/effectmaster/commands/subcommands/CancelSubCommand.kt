package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.effect.EditEffectGui
import me.m64diamondstar.effectmaster.editor.show.ShowSettingsGui
import me.m64diamondstar.effectmaster.editor.utils.EditingPlayers
import me.m64diamondstar.effectmaster.editor.utils.SettingsPlayers
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CancelSubCommand: SubCommand {

    override fun getName(): String {
        return "cancel"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(emComponent("<prefix><error>You can only use this command as a player."))
            return
        }

        if(args.size == 1){
            if(!EditingPlayers.contains(sender) && !SettingsPlayers.contains(sender)) {
                sender.sendMessage(emComponent("<prefix><error>You aren't editing anything."))
                return
            }

            if(EditingPlayers.contains(sender)) {
                val showCategory = EditingPlayers.get(sender)!!.first.getCategory()
                val showName = EditingPlayers.get(sender)!!.first.getName()
                val effectShow = EffectShow(showCategory, showName)

                val id = EditingPlayers.get(sender)!!.second

                val editEffectGui = EditEffectGui(sender, id, effectShow, 0)
                editEffectGui.open()
                EditingPlayers.remove(sender)
            }else{
                val showCategory = SettingsPlayers.get(sender)!!.first.getCategory()
                val showName = SettingsPlayers.get(sender)!!.first.getName()
                val effectShow = EffectShow(showCategory, showName)

                val settingsGui = ShowSettingsGui(sender, effectShow)
                settingsGui.open()
                SettingsPlayers.remove(sender)
            }

            sender.sendMessage(emComponent("<prefix><success>Cancelled edit."))
        }
        else {
            DefaultResponse.helpCancel(sender)
        }
    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

}