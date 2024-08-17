package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.utils.Prefix
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LocationSubCommand: SubCommand {
    override fun getName(): String {
        return "location"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You can only use this command as a player."))
            return
        }

        val playerTargetBlock = sender.getTargetBlockExact(50)

        val playerLocation = sender.location
        val playerBlockLocation = sender.location.block.location
        val playerTopLocation = sender.location.clone().add(0.0, 2.0, 0.0)

        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))


        var clickableComponent = TextComponent(TextComponent("Click here to copy your location."))
        clickableComponent.color = ChatColor.of(Colors.Color.DEFAULT.toString())
        clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, LocationUtils.getStringFromLocation(
            playerLocation,
            false,
            true
        ))
        clickableComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder("Click to copy \"${LocationUtils.getStringFromLocation(playerLocation, false, true)}\"").create())
        sender.spigot().sendMessage(clickableComponent)




        clickableComponent = TextComponent(TextComponent("Click here to copy your block location."))
        clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
        clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, LocationUtils.getStringFromLocation(
            playerBlockLocation,
            true,
            true
        ))
        clickableComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder("Click to copy \"${LocationUtils.getStringFromLocation(playerBlockLocation, true, true)}\"").create())
        sender.spigot().sendMessage(clickableComponent)





        clickableComponent = TextComponent(TextComponent("Click here to copy your top location."))
        clickableComponent.color = ChatColor.of(Colors.Color.DEFAULT.toString())
        clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, LocationUtils.getStringFromLocation(
            playerTopLocation,
            false,
            true
        ))
        clickableComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
            ComponentBuilder("Click to copy \"${LocationUtils.getStringFromLocation(playerTopLocation, false, true)}\"").create())
        sender.spigot().sendMessage(clickableComponent)





        if(playerTargetBlock != null && playerTargetBlock.type != Material.AIR){
            val playerTargetLocation = playerTargetBlock.location
            clickableComponent = TextComponent(TextComponent("Click here to copy the location of the block you're looking at."))
            clickableComponent.color = ChatColor.of(Colors.Color.BACKGROUND.toString())
            clickableComponent.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, LocationUtils.getStringFromLocation(
                playerTargetLocation,
                true,
                true
            ))
            clickableComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("Click to copy \"${
                    LocationUtils.getStringFromLocation(
                    playerTargetLocation,
                    true,
                    true
                )}\"").create())
            sender.spigot().sendMessage(clickableComponent)
        }
        sender.sendMessage(Colors.format(Prefix.PrefixType.BACKGROUND.toShortString() + "-=<❄>=-"))

    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }
}