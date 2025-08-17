package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayAtSubCommand: SubCommand {
    override fun getName(): String {
        return "playat"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(args.size == 7){
            if(!DefaultResponse.existsShow(sender, args))
                return

            // /em playat <category> <show> world, x, y, z
            val location = LocationUtils.getLocationFromString(args.slice(3..6).joinToString(" "))
            if(location == null){
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The location you entered couldn't be found."))
                return
            }

            val effectShow = EffectShow(args[1], args[2])

            if(effectShow.centerLocation == null) {
                sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The center location couldn't be found. " +
                        "Please set the center location in the editor in the settings section."))
                return
            }
            effectShow.play(null, location)
            sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Successfully started this show."))

        }else DefaultResponse.helpPlay(sender)
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
            if(sender !is Player) return tabs
            tabs.add(LocationUtils.getStringFromLocation(sender.location, false, true) ?: "ERROR - Location not found...")
        }

        return tabs
    }
}