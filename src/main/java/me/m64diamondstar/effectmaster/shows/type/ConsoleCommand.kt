package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
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

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "CONSOLE_COMMAND"))
        list.add(Pair("Command", "say Hey there!"))
        list.add(Pair("Delay", 0))
        return list
    }

}