package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class ConsoleCommand() : Effect() {
    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        val command = getSection(effectShow, id).getString("Command") ?: return
        if(command.startsWith("/"))
            command.drop(1)

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
    }

    override fun getIdentifier(): String {
        return "CONSOLE_COMMAND"
    }

    override fun getDisplayMaterial(): Material {
        return Material.COMMAND_BLOCK
    }

    override fun getDescription(): String {
        return "Executes a console command."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Command", "say EffectMaster is the coolest plugin!", "The command to execute, without a slash in front. For example \"say Hello everyone!\".", {it}) { true })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }
}