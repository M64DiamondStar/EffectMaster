package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.utils.*
import org.bukkit.Material
import org.bukkit.entity.Player

class PlayShow : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        val category = getSection(effectShow, id).getString("Category") ?: return
        val show = getSection(effectShow, id).getString("Show") ?: return
        val from = getSection(effectShow, id).getInt("From")

        if(!ShowUtils.existsCategory(category)){
            throw InvalidParameterException("The category $category does not exist!")
        }

        if(!ShowUtils.existsShow(category, show)){
            throw InvalidParameterException("The show $show does not exist in category $category!")
        }

        val newEffectShow = EffectShow(category, show)
        if(from <= 0) {
            effectShow.addChildShow(newEffectShow)
            newEffectShow.play(null)
        }
        else {
            val result = newEffectShow.playFrom(from, null)
            if (!result) {
                throw InvalidParameterException("The ID $from does not exist in the show $show from category $category!")
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

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(Parameter(
            "Category",
            "category",
            "The category in which the show is.",
            {it},
            { ShowUtils.existsCategory(it) })
        )
        list.add(Parameter("Show", "show", "The show to play.", {it}, { true })) // Can't check if it exists with only the name
        list.add(Parameter(
            "From",
            0,
            "The ID from which the show should be played. If this is 0 or lower, the show will be played from the start.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }

}