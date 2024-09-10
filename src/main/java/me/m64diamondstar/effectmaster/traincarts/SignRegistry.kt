package me.m64diamondstar.effectmaster.traincarts

import com.bergerkiller.bukkit.tc.signactions.SignAction
import me.m64diamondstar.effectmaster.traincarts.signs.SignActionPlayShow

object SignRegistry {

    private val signActionPlayShow = SignActionPlayShow()

    fun registerSigns(){
        SignAction.register(signActionPlayShow)
    }

    fun unregisterSigns(){
        SignAction.unregister(signActionPlayShow)
    }

}