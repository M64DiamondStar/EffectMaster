package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PrivatePlaySubCommand: SubCommand {
    override fun getName(): String {
        return "privateplay"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(!EffectMaster.isProtocolLibLoaded){
            sender.sendMessage(emComponent("<prefix><error>You need to have ProtocolLib installed on your server " +
                    "to use this sub-command."))
            return
        }
        if(args.size > 3){
            if(!DefaultResponse.existsShow(sender, args))
                return

            val sb = StringBuilder()
            for (loopArgs in 3 until args.size) {
                sb.append(args[loopArgs]).append(" ")
            }

            val players = ArrayList<Player>()

            try {
                EffectMaster.plugin().server.selectEntities(sender, sb.toString().dropLast(1))
                    .forEach { if (it is Player) players.add(it) }
            }catch (_: IllegalArgumentException){
                sender.sendMessage(emComponent("<prefix><error>The selector you entered couldn't be processed."))
                sender.sendMessage(emComponent("<short_prefix><default>Information about selectors here:"))
                sender.sendMessage(emComponent("<short_prefix><default>https://minecraft.fandom.com/wiki/Target_selectors"))
                return
            }
            val effectShow = EffectShow(args[1], args[2])
            effectShow.play(players)
            sender.sendMessage(emComponent("<prefix><success>Successfully started this show."))
        }else{
            DefaultResponse.helpPrivatePlay(sender)
        }
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
            tabs.add("@p")
            tabs.add("@a")
            tabs.add("@r")
            tabs.add("@s")
            for(player in Bukkit.getOnlinePlayers()){
                tabs.add(player.name)
            }
        }

        return tabs
    }
}