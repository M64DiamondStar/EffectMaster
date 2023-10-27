package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.entity.Player

class PlayShow(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {
        val category = getSection().getString("Category") ?: return
        val show = getSection().getString("Show") ?: return
        val from = getSection().getInt("From")

        if(!ShowUtils.existsCategory(category)){
            EffectMaster.plugin.logger.warning("Couldn't play Play Show with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The category $category does not exist.")
            return
        }

        if(!ShowUtils.existsShow(category, show)){
            EffectMaster.plugin.logger.warning("Couldn't play Play Show with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The show $show in the category $category does not exist.")
            return
        }

        val effectShow = EffectShow(category, show, null)
        if(from <= 0) {
            effectShow.play()
        }
        else {
            val result = effectShow.playFrom(from)
            if (!result) {
                EffectMaster.plugin.logger.warning("Couldn't play Play Show with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
                EffectMaster.plugin.logger.warning("The effect tried to play the show from ID $from, but this ID does not exist!")
            }
        }
    }

    override fun getType(): Type {
        return Type.PLAY_SHOW
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "PLAY_SHOW"))
        list.add(Pair("Category", "category"))
        list.add(Pair("Show", "show"))
        list.add(Pair("From", 0))
        list.add(Pair("Delay", 0))
        return list
    }

}