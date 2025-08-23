package me.m64diamondstar.effectmaster.hooks.traincarts.signs

import com.bergerkiller.bukkit.tc.events.SignActionEvent
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent
import com.bergerkiller.bukkit.tc.signactions.SignAction
import com.bergerkiller.bukkit.tc.signactions.SignActionType
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions
import me.m64diamondstar.effectmaster.ktx.emComponent
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils

class SignActionPlayShow: SignAction() {

    override fun match(info: SignActionEvent): Boolean {
        return (info.isType("playshow") || info.isType("emshow"))
    }

    override fun execute(info: SignActionEvent) {
        if (info.isAction(SignActionType.GROUP_ENTER) && info.isPowered) {

            val args = if(info.getLine(1).contains('-')) info.getLine(1).split("-")[1] else null

            if(args == null) {
                val effectShow = EffectShow(info.getLine(2), info.getLine(3))
                effectShow.play(null)
            }else{

                val whenPassengers = args.contains('p') // Only play show when there are passengers

                if(whenPassengers && info.members.any { it.entity.playerPassengers.isNotEmpty() }) {
                    val effectShow = EffectShow(info.getLine(2), info.getLine(3))
                    effectShow.play(null)
                }

            }
        }
    }

    override fun build(event: SignChangeActionEvent): Boolean {

        event.lines.forEach {
            if(it.isEmpty()) {
                event.player.sendMessage(emComponent("<prefix><error>Please use this format:"))
                event.player.sendMessage(emComponent("<prefix><error>  [train]"))
                event.player.sendMessage(emComponent("<prefix><error>    playshow"))
                event.player.sendMessage(emComponent("<prefix><error><category>"))
                event.player.sendMessage(emComponent("<prefix><error>   <name>"))
                return false
            }
        }

        if(!ShowUtils.existsCategory(event.getLine(2)) || !ShowUtils.existsShow(event.getLine(2), event.getLine(3))){
            event.player.sendMessage(emComponent("<prefix><error>This show or category does not exist."))
            return false
        }

        return SignBuildOptions.create()
            .setName("EffectMaster Play Show")
            .setDescription("play a show when this gets activated by a train")
            .handle(event.player)
    }
}