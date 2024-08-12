package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class Animatronic(): Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        if(!EffectMaster.isAnimatronicsLoaded){
            EffectMaster.plugin().logger.warning("The show \"${effectShow.getName()}\" in category \"${effectShow.getCategory()}\" " +
                    "tried to play an Animatronic at ID $id while that plugin is not enabled. Please add the plugin to the server or remove the effect.")
            return
        }

        val name = getSection(effectShow, id).getString("Name") ?: return
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "anima play $name")
    }

    override fun getIdentifier(): String{
        return "ANIMATRONIC"
    }

    override fun getDisplayMaterial(): Material {
        return Material.ARMOR_STAND
    }

    override fun getDescription(): String {
        return "Plays an animatronic."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "ANIMATRONIC"))
        list.add(Pair("Name", "anima"))
        list.add(Pair("Delay", 0))
        return list
    }
}