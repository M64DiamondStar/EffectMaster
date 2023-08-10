package me.m64diamondstar.effectmaster.editor.utils

import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.Effect
import org.bukkit.entity.Player

object EditorUtils {

    fun filterDefaults(player: Player, effect: Effect): List<Pair<String, Any>>{
        val filtered = ArrayList<Pair<String, Any>>()

        effect.getDefaults().forEach {
            when (it.first){
                "Location" -> {
                    filtered.add(Pair("Location",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true)
                    ))
                }

                "FromLocation" -> {
                    filtered.add(Pair("FromLocation",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true)
                    ))
                }

                "ToLocation" -> {
                    filtered.add(Pair("ToLocation",
                        LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = true)
                    ))
                }

                "Path" -> {
                    filtered.add(Pair("Path", LocationUtils.getStringFromLocation(
                        player.location,
                        asBlock = false,
                        withWorld = true
                    ) + "; "
                            + LocationUtils.getStringFromLocation(player.location, asBlock = false, withWorld = false)
                    ))
                }

                else -> {
                    filtered.add(Pair(it.first, it.second))
                }
            }
        }

        return filtered
    }

}