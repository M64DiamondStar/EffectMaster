package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import org.bukkit.entity.Player

object EditorUtils {

    fun filterPlayerDefaults(player: Player, effect: Effect): List<ParameterLike>{
        val filtered = ArrayList<ParameterLike>()
        filtered.add(Parameter("Type", effect.getIdentifier(), "", {it}, { true })) // Never checked, so description and verification not needed
        effect.getDefaults().forEach {
            when (it.name){
                "Location" -> { // Never checked, so description and verification not needed
                    filtered.add(Parameter(
                        "Location",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true)!!,
                        "",
                        {it},
                        { true })
                    )
                }

                "FromLocation" -> { // Never checked, so description and verification not needed
                    filtered.add(Parameter(
                        "FromLocation",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true)!!,
                        "",
                        {it},
                        { true })
                    )
                }

                "ToLocation" -> { // Never checked, so description and verification not needed
                    filtered.add(Parameter(
                        "ToLocation",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true)!!,
                        "",
                        {it},
                        { true })
                    )
                }

                "Path" -> { // Never checked, so description and verification not needed
                    filtered.add(Parameter(
                        "Path",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true) + "; "
                                + LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = false),
                        "",
                        {it},
                        { true })
                    )
                }

                else -> {
                    filtered.add(it)
                }
            }
        }

        return filtered
    }

    fun getDefaults(effect: Effect): List<ParameterLike>{
        val filtered = ArrayList<ParameterLike>()
        filtered.add(Parameter("Type", effect.getIdentifier(), "", {it}, { true })) // Never checked, so description and verification not needed
        filtered.addAll(effect.getDefaults())
        return filtered
    }

}