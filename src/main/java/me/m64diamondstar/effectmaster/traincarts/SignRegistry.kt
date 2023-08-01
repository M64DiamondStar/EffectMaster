package me.m64diamondstar.effectmaster.traincarts

import com.bergerkiller.bukkit.tc.signactions.SignAction
import me.m64diamondstar.effectmaster.traincarts.signs.SignActionEmShow
import me.m64diamondstar.effectmaster.traincarts.signs.SignActionPlayShow

object SignRegistry {

    private val signActionPlayShow = SignActionPlayShow()
    private val signActionEmShow = SignActionEmShow()

    fun registerSigns(){
        SignAction.register(signActionPlayShow)
        SignAction.register(signActionEmShow)
    }

    fun unregisterSigns(){
        SignAction.unregister(signActionPlayShow)
        SignAction.unregister(signActionEmShow)
    }

}