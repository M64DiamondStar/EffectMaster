package me.m64diamondstar.effectmaster.shows

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.scheduler.BukkitRunnable

object ShowLooper {

    private val loops = HashMap<Pair<String, String>, Pair<Long, Long>>()

    /**
     * Starts the looping of a show.
     */
    fun loop(show: EffectShow) {
        loops[Pair(show.getCategory(), show.getName())] = Pair(show.loopingDelay, show.loopingInterval)
    }

    /**
     * Stops the looping of a show.
     */
    fun stopLoop(show: EffectShow) {
        loops.remove(Pair(show.getCategory(), show.getName()))
    }

    /**
     * Updates the looping of a show. This is needed when a setting has been changed.
     */
    fun updateLoop(show: EffectShow) {
        if(loops.containsKey(Pair(show.getCategory(), show.getName())))
            loops[Pair(show.getCategory(), show.getName())] = Pair(show.loopingDelay, show.loopingInterval)
    }

    /**
     * This method should only be called in EffectMaster's onEnable
     */
    fun initialize() {
        ShowUtils.getCategories().forEach { categoryFile ->
            ShowUtils.getShows(categoryFile.name).forEach { showFile ->
                val effectShow = EffectShow(categoryFile.name, showFile.nameWithoutExtension)
                if(effectShow.looping)
                    loop(effectShow)
            }
        }

        // Create a task that runs every tick
        object : BukkitRunnable() {
            var c = 0L

            override fun run() {
                c++

                // Iterate through each entry in the loops hashmap
                for ((key, value) in loops) {
                    val (categoryName, showName) = key
                    val (loopingDelay, loopingInterval) = value

                    if (loopingInterval > 0 && c >= loopingDelay && (c - loopingDelay) % loopingInterval == 0L) {
                        val show = EffectShow(categoryName, showName)
                        show.play(null)
                    }
                }
            }
        }.runTaskTimer(EffectMaster.plugin(), 0, 1)
    }

}