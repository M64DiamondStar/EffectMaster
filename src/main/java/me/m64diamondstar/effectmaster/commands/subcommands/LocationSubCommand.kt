package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LocationSubCommand: SubCommand {
    override fun getName(): String {
        return "location"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(emComponent("<prefix><error>You can only use this command as a player."))
            return
        }

        val playerTargetBlock = sender.getTargetBlockExact(50)

        val playerLocation = sender.location
        val playerBlockLocation = sender.location.block.location
        val playerTopLocation = sender.location.clone().add(0.0, 2.0, 0.0)

        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))

        var clickableComponent = emComponent("<default>Click here to copy your location.")
            .clickEvent(ClickEvent.copyToClipboard(LocationUtils.getStringFromLocation(playerLocation,
                asBlock = false,
                withWorld = true
            )!!))
            .hoverEvent(HoverEvent.showText(Component.text("Click to copy \"${LocationUtils.getStringFromLocation(playerLocation,
                asBlock = false,
                withWorld = true
            )}\"")))
        sender.sendMessage(clickableComponent)

        clickableComponent = emComponent("<background>Click here to copy your block location.")
            .clickEvent(ClickEvent.copyToClipboard(LocationUtils.getStringFromLocation(playerBlockLocation,
                asBlock = true,
                withWorld = true
            )!!))
            .hoverEvent(HoverEvent.showText(Component.text("Click to copy \"${LocationUtils.getStringFromLocation(playerBlockLocation,
                asBlock = true,
                withWorld = true
            )}\"")))
        sender.sendMessage(clickableComponent)

        clickableComponent = emComponent("<default>Click here to copy your top location.")
            .clickEvent(ClickEvent.copyToClipboard(LocationUtils.getStringFromLocation(playerTopLocation,
                asBlock = false,
                withWorld = true
            )!!))
            .hoverEvent(HoverEvent.showText(Component.text("Click to copy \"${LocationUtils.getStringFromLocation(playerTopLocation,
                asBlock = false,
                withWorld = true
            )}\"")))
        sender.sendMessage(clickableComponent)

        if(playerTargetBlock != null && playerTargetBlock.type != Material.AIR){
            val playerTargetLocation = playerTargetBlock.location
            clickableComponent = emComponent("<background>Click here to copy the location of the block you're looking at.")
                .clickEvent(ClickEvent.copyToClipboard(LocationUtils.getStringFromLocation(playerTargetLocation,
                    asBlock = true,
                    withWorld = true
                )!!))
                .hoverEvent(HoverEvent.showText(Component.text("Click to copy \"${LocationUtils.getStringFromLocation(playerTargetLocation,
                    asBlock = true,
                    withWorld = true
                )}\"")))
            sender.sendMessage(clickableComponent)
        }
        sender.sendMessage(emComponent("<short_prefix><background>-=<❄>=-"))

    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }
}