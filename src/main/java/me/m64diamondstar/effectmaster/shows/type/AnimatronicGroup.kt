package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.thundertnt33.animatronics.api.Group

class AnimatronicGroup(show: Show, private val id: Int) : Effect(show, id) {

    override fun execute() {
        if(!EffectMaster.isAnimatronicsLoaded){
            EffectMaster.plugin.logger.warning("The show \"${getShow().getName()}\" in category \"${getShow().getCategory()}\" " +
                    "tried to play an Animatronic Group at ID $id while that plugin is not enabled. Please add the plugin to the server or remove the effect.")
            return
        }

        val name = getSection().getString("Name")!!
        val group = Group(name)
        group.play()
    }

    override fun getType(): Type {
        return Type.ANIMATRONIC_GROUP
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "ANIMATRONIC_GROUP"))
        list.add(Pair("Name", "anima"))
        list.add(Pair("Delay", 0))
        return list
    }
}