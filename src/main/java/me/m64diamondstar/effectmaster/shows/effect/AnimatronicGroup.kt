package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class AnimatronicGroup() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        if(!EffectMaster.isAnimatronicsLoaded){
            EffectMaster.plugin().logger.warning("The show \"${effectShow.getName()}\" in category \"${effectShow.getCategory()}\" " +
                    "tried to play an Animatronic Group at ID $id while that plugin is not enabled. Please add the plugin to the server or remove the effect.")
            return
        }

        val name = getSection(effectShow, id).getString("Name") ?: return
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "anima group $name play")
    }

    override fun getIdentifier(): String {
        return "ANIMATRONIC_GROUP"
    }

    override fun getDisplayMaterial(): Material {
        return Material.ARMOR_STAND
    }

    override fun getDescription(): String {
        return "Plays an animatronic group."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Name", "anima", "The name of the animatronic group to play.", {it}, { true }))
        list.add(Parameter(
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}