package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Material
import org.bukkit.entity.Player

class PlayShow() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        val category = getSection(effectShow, id).getString("Category") ?: return
        val show = getSection(effectShow, id).getString("Show") ?: return
        val from = getSection(effectShow, id).getInt("From")

        if(!ShowUtils.existsCategory(category)){
            EffectMaster.plugin().logger.warning("Couldn't play Play Show with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The category $category does not exist.")
            return
        }

        if(!ShowUtils.existsShow(category, show)){
            EffectMaster.plugin().logger.warning("Couldn't play Play Show with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The show $show in the category $category does not exist.")
            return
        }

        val effectShow = EffectShow(category, show)
        if(from <= 0) {
            effectShow.play(null)
        }
        else {
            val result = effectShow.playFrom(from, null)
            if (!result) {
                EffectMaster.plugin().logger.warning("Couldn't play Play Show with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The effect tried to play the show from ID $from, but this ID does not exist!")
            }
        }
    }

    override fun getIdentifier(): String {
        return "PLAY_SHOW"
    }

    override fun getDisplayMaterial(): Material {
        return Material.NETHER_STAR
    }

    override fun getDescription(): String {
        return "Plays another EffectMaster show."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Category", "category", "The category in which the show is.", {it}) { ShowUtils.existsCategory(it) })
        list.add(Parameter("Show", "show", "The show to play.", {it}) { true }) // Can't check if it exists with only the name
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }

}