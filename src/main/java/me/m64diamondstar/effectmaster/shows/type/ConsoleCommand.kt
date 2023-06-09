package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ConsoleCommand(effectShow: EffectShow, id: Int) : Effect(effectShow, id) {
    override fun execute(players: List<Player>?) {
        val command = getSection().getString("Command") ?: return
        if(command.startsWith("/"))
            command.drop(1)

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
    }

    override fun getType(): Type {
        return Type.CONSOLE_COMMAND
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