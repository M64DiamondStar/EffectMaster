package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.EffectType
import me.m64diamondstar.effectmaster.shows.utils.Show

class Animatronic(show: Show, private val id: Int): EffectType(show, id) {

    override fun execute() {
        if(!EffectMaster.isAnimatronicsLoaded){
            EffectMaster.plugin.logger.warning("The show \"${getShow().getName()}\" in category \"${getShow().getCategory()}\" " +
                    "tried to play an Animatronic at ID $id while that plugin is not enabled. Please add the plugin to the server or remove the effect.")
            return
        }

        val name = getSection().getString("Name")!!
        val animatronic = me.thundertnt33.animatronics.api.Animatronic(name)
        animatronic.start()
    }

    override fun getType(): Types{
        return Types.ANIMATRONIC
    }

    override fun isSync(): Boolean {
        return true
    }
}