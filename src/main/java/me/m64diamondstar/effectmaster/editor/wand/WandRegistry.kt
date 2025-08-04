package me.m64diamondstar.effectmaster.editor.wand

object WandRegistry {

    private val wands = ArrayList<Wand>()

    fun registerWand(wand: Wand){
        wands.add(wand)
    }

    fun unregisterWand(wand: Wand){
        wands.remove(wand)
    }

    fun getRegisteredWands(): List<Wand> = wands

}