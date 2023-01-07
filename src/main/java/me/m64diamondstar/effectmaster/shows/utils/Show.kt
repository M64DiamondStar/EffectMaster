package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.data.Configuration
import org.bukkit.scheduler.BukkitRunnable
import java.lang.IllegalArgumentException
import java.util.ArrayList

/**
 * Play custom shows in the park, can be used in coasters and other rides for fun decoration.
 */
class Show(private val category: String, private val name: String): Configuration("shows/$category", name) {

    /**
     * Adds the standard comments to the configuration file of this show.
     */
    fun createShow(){
        val header = ArrayList<String>()
        header.add("-----------------------------------------")
        header.add("This is the file for the show: ${getName()}.")
        header.add(" ")
        header.add("Reminder, all the times are in ticks! 20 ticks = 1 second.")
        header.add("For extra information, check the wiki:")
        header.add("https://github.com/M64DiamondStar/EffectMaster/wiki/Effect")
        header.add("-----------------------------------------")

        this.getConfig().options().setHeader(header)

        this.reloadConfig()
    }

    fun deleteShow(){
        this.deleteFile()
    }

    fun getIDAmount(): Int {
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

                if(tasksDone >= getIDAmount()){
                    this.cancel()
                    return
                }

                var i = 1
                while (getConfig().getConfigurationSection("$i") != null) {
                    if (getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
                        getType(i)!!.execute()
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

                if(tasksDone >= getIDAmount()){
                    this.cancel()
                    return
                }

                var i = id
                while (getConfig().getConfigurationSection("$i") != null) {
                    if (getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
                        getType(i)!!.execute()
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
        getType(id)?.execute()
        return true
    }

    fun getCategory(): String{
        return category
    }

    fun getName(): String{
        return name
    }

    fun getType(id: Int): EffectType? {
        val type: EffectType.Types

        val string = if(getConfig().get("$id.Type") != null) getConfig().getString("$id.Type")!! else return null

        try{
            type = EffectType.Types.valueOf(string.uppercase())
        }catch (e: IllegalArgumentException){
            return null
        }

        return type.getTypeClass(this, id)
    }

}