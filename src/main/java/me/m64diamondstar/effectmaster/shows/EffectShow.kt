package me.m64diamondstar.effectmaster.shows

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.data.Configuration
import me.m64diamondstar.effectmaster.shows.utils.Effect
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * Play custom effect shows.
 * @param category The category the show is located in.
 * @param name The name of the show.
 * @param players The list of players the show will be displayed to. If set to null, it'll display for everyone.
 */
class EffectShow(private val category: String, private val name: String, private val players: List<Player>?): Configuration("shows/$category", name) {

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

    fun deleteEffect(id: Int){
        val keys = this.getConfig().getKeys(false).toList()

        for(i in id until keys.size) {
            val currentSection = this.getConfig().getConfigurationSection("$i")
            val nextSection = this.getConfig().getConfigurationSection("${i + 1}")

            currentSection?.getValues(true)?.forEach{
                currentSection.set(it.key, null)
            }

            nextSection?.getValues(true)?.forEach{
                currentSection?.set(it.key,it.value)
            }
        }
        this.getConfig().set("${keys.size}", null)
        this.reloadConfig()
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
                        getEffect(i)?.execute(players, this@EffectShow, i)
                        tasksDone++
                    }
                    i++
                }

                count++
            }
        }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
    }

    /**
     * Starts the show from a specific moment.
     * @param id the ID from where to start the show.
     * @return Whether the show was started successfully.
     */
    fun playFrom(id: Int): Boolean{
        if(getConfig().getConfigurationSection("$id") == null) return false
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
                        getEffect(i)?.execute(players, this@EffectShow, i)
                        tasksDone++
                    }
                    i++
                }

                count++
            }
        }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        return true
    }

    /**
     * Only plays the effect with the given id.
     * @param id the ID of the effect that should be played.
     */
    fun playOnly(id: Int): Boolean{
        if(getConfig().getConfigurationSection("$id") == null) return false
        getEffect(id)?.execute(players, this, id)
        return true
    }

    fun getCategory(): String{
        return category
    }

    fun getName(): String{
        return name
    }

    fun setDefaults(id: Int, defaults: List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>){
        for(pair in defaults){
            this.getConfig().set("$id.${pair.first}", pair.second)
        }
        this.reloadConfig()
    }

    fun getAllEffects(): HashMap<Int, Effect?>{
        val map = HashMap<Int, Effect?>()
        for(id in 1..getMaxId()){
            map[id] = getEffect(id)
        }
        return map
    }

    fun getEffect(id: Int): Effect? {
        return try{
            Effect.Type.getEffect(getConfig().getString("$id.Type")!!.uppercase())
        }catch (_: Exception){
            null
        }
    }

}