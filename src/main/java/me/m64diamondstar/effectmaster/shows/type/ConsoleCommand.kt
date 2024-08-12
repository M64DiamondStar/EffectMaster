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

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "CONSOLE_COMMAND"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Command", "say Hey there!"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }

}