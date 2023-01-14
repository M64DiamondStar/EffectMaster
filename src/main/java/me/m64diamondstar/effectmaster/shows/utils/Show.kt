package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.data.Configuration
import org.bukkit.scheduler.BukkitRunnable

/**
 * Play custom shows in the park, can be used in coasters and other rides for fun decoration.
 */
class Show(private val category: String, private val name: String): Configuration("shows/$category", name) {

    /**
     * Adds the standard comments to the configuration file of this show.
     */
    fun createShow(){
        if(EffectMaster.shortServerVersion() >= 18) { // Configuration Comments don't work for 1.17 and lower
            val header = ArrayList<String>()
            header.add("-----------------------------------------")
            header.add("This is the file for the show: ${getName()}.")
            header.add(" ")
            header.add("Reminder, all the times are in ticks! 20 ticks = 1 second.")
            header.add("For extra information, check the wiki:")
            header.add("https://github.com/M64DiamondStar/EffectMaster/wiki/Effect")
            header.add("-----------------------------------------")

            this.getConfig().options().setHeader(header)
        }

        this.reloadConfig()
    }

    fun deleteShow(){
        this.deleteFile()
    }

    fun getMaxId(): Int {
        var i = 1
        while (getConfig().getConfigurationSection("$i") != null) {
            i++
        }
        return i - 1
    }

    /**
     * Plays the full show.
     */
    fun play(){
        object: BukkitRunnable(){
            var count = 0L
            var tasksDone = 0
            override fun run() {

                if(tasksDone >= getMaxId()){
                    this.cancel()
                    return
                }

                var i = 1
                while (getConfig().getConfigurationSection("$i") != null) {
                    if (getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
                        getEffect(i)?.execute()
                        tasksDone++
                    }
                    i++
                }

                count++
            }
        }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
    }

    /**
     * Starts the show from a specific moment.
     * @param id the ID from where to start the show.
     */
    fun playFrom(id: Int){
        object: BukkitRunnable(){
            var count = 0L
            var tasksDone = 0
            override fun run() {

                if(tasksDone >= getMaxId()){
                    this.cancel()
                    return
                }

                var i = id
                while (getConfig().getConfigurationSection("$i") != null) {
                    if (getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
                        getEffect(i)?.execute()
                        tasksDone++
                    }
                    i++
                }

                count++
            }
        }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
    }

    /**
     * Only plays the effect with the given id.
     * @param id the ID of the effect that should be played.
     */
    fun playOnly(id: Int): Boolean{
        if(getConfig().getConfigurationSection("$id") == null) return false
        getEffect(id)?.execute()
        return true
    }

    fun getCategory(): String{
        return category
    }

    fun getName(): String{
        return name
    }

    fun getEffectsSortedByDelay(): List<Effect> {
        return getAllEffects().sortedBy { it.getDelay() }
    }

    fun getAllEffects(): List<Effect>{
        val list = ArrayList<Effect>()
        for(id in 1..getMaxId()){
            getEffect(id)?.let { list.add(it) }
        }
        return list
    }

    fun getEffect(id: Int): Effect? {
        return try{
            Effect.Type.valueOf(getConfig().getString("$id.Type")!!.uppercase()).getTypeClass(this, id)
        }catch (e: IllegalArgumentException){
            null
        }
    }

}