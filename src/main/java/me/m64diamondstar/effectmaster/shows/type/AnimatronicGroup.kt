package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.EffectType
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.thundertnt33.animatronics.api.Group

class AnimatronicGroup(show: Show, id: Int) : EffectType(show, id) {

    override fun execute() {
        val name = getSection().getString("Name")!!
        val group = Group(name)
        group.play()
    }

    override fun getType(): Types {
        return Types.ANIMATRONIC_GROUP
    }

    override fun isSync(): Boolean {
        return true
    }
}