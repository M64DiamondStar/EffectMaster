package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.EffectType
import me.m64diamondstar.effectmaster.shows.utils.Show

class Animatronic(show: Show, id: Int): EffectType(show, id) {

    override fun execute() {
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