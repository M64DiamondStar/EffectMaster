package me.m64diamondstar.effectmaster.shows

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Bukkit

object ShowLooper {

    private val loops = HashMap<Pair<String, String>, Pair<Long, Long>>()
    private var currentTick: Long = 0L

    /**
     * Starts the looping of a show.
     * @param show the show you want to enable looping for
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
     * Checks for a given category and name whether the show is looping.
     */
    fun isLooping(category: String, name: String): Boolean {
        return loops.containsKey(Pair(category, name))
    }

    /**
     * Updates the looping of a show. This is needed when a setting has been changed.
     */
    fun updateLoop(show: EffectShow) {
        if(loops.containsKey(Pair(show.getCategory(), show.getName())))
            loops[Pair(show.getCategory(), show.getName())] = Pair(show.loopingDelay, show.loopingInterval)
    }

    /**
     * Get all the currently looping shows
     */
    fun getLooping(): Set<Pair<String, String>> = loops.keys

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

        val server = Bukkit.getServer()
        val plugin = EffectMaster.plugin()

        server.globalRegionScheduler.runAtFixedRate(plugin, { _ ->
            currentTick++

            for ((key, value) in loops) {
                val (categoryName, showName) = key
                val (loopingDelay, loopingInterval) = value

                if (loopingInterval > 0L &&
                    currentTick >= loopingDelay &&
                    (currentTick - loopingDelay) % loopingInterval == 0L
                ) {
                    val show = EffectShow(categoryName, showName)
                    show.play(null)
                }
            }
        }, 1L, 1L)
    }

}