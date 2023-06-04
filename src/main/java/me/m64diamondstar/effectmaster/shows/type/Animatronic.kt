package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.EffectShow
import org.bukkit.entity.Player

class Animatronic(effectShow: EffectShow, private val id: Int): Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {
        if(!EffectMaster.isAnimatronicsLoaded){
            EffectMaster.plugin.logger.warning("The show \"${getShow().getName()}\" in category \"${getShow().getCategory()}\" " +
                    "tried to play an Animatronic at ID $id while that plugin is not enabled. Please add the plugin to the server or remove the effect.")
            return
        }

        val name = getSection().getString("Name") ?: return
        val animatronic = me.thundertnt33.animatronics.api.Animatronic(name)
        animatronic.start()
    }

    override fun getType(): Type{
        return Type.ANIMATRONIC
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