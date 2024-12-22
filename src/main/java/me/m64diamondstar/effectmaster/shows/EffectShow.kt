package me.m64diamondstar.effectmaster.shows

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.data.Configuration
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Play custom effect shows.
 * @param category The category the show is located in.
 * @param name The name of the show.
 */
class EffectShow(private val category: String, private val name: String): Configuration("shows/$category", name) {

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
            header.add("https://effectmaster.m64.dev/")
            header.add("-----------------------------------------")

            this.getConfig().options().setHeader(header)
        }

        this.getConfig().set("Settings.Looping", false)
        this.getConfig().set("Settings.Looping-Delay", 0)
        this.getConfig().set("Settings.Looping-Interval", 200)

        this.reloadConfig()
    }

    fun deleteShow(){
        this.deleteFile()
    }

    fun deleteEffect(id: Int){
        val keys = this.getConfig().getKeys(false).toMutableList()
        keys.remove("Settings")

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
    fun play(players: List<Player>?) {
        play(players, null, false)
    }

    fun play(players: List<Player>?, at: Location?, reverse: Boolean){
        val settings = HashSet<ShowSetting>()
        if(at != null) settings.add(ShowSetting(ShowSetting.Identifier.PLAY_AT, at))

        var count = 0L
        var tasksDone = 0

        EffectMaster.getFoliaLib().scheduler.runTimer( { task ->
            if(tasksDone >= getMaxId()){
                task.cancel()
                return@runTimer
            }

            var i = 1
            while (getConfig().getConfigurationSection("$i") != null) {
                if (getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
                    getEffect(i)?.execute(players, this@EffectShow, i, settings)
                    tasksDone++
                }
                i++
            }

            count++
        }, 0L, 0L)
    }

    /**
     * Starts the show from a specific moment.
     * @param id the ID from where to start the show.
     * @return Whether the show was started successfully.
     */
    fun playFrom(id: Int, players: List<Player>?): Boolean{
        if(getConfig().getConfigurationSection("$id") == null) return false

        var count = 0L
        var tasksDone = 0
        EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
            if(tasksDone >= getMaxId()){
                task.cancel()
                return@runTimer
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
        }, 0L, 1L)
        return true
    }

    /**
     * Only plays the effect with the given id.
     * @param id the ID of the effect that should be played.
     */
    fun playOnly(id: Int, players: List<Player>?): Boolean{
        if(getConfig().getConfigurationSection("$id") == null) return false
        getEffect(id)?.execute(players, this, id)
        return true
    }

    fun getCategory(): String{
        return category
    }

    fun getName(): String{
        return name.replace(".yml", "")
    }

    fun setDefaults(id: Int, defaults: List<Parameter>){
        for(pair in defaults){
            this.getConfig().set("$id.${pair.name}", pair.defaultValue)
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

    var looping: Boolean
        get() {
            return this.getConfig().getBoolean("Settings.Looping")
        }
        set(value) {
            this.getConfig().set("Settings.Looping", value)
            this.reloadConfig()
        }

    var loopingDelay: Long
        get() {
            return this.getConfig().getLong("Settings.Looping-Delay")
        }
        set(value) {
            this.getConfig().set("Settings.Looping-Delay", value)
            this.reloadConfig()
        }

    var loopingInterval: Long
        get() {
            return this.getConfig().getLong("Settings.Looping-Interval")
        }
        set(value) {
            this.getConfig().set("Settings.Looping-Interval", value)
            this.reloadConfig()
        }

    var centerLocation: Location?
        get() {
            return LocationUtils.getLocationFromString(this.getConfig().getString("Settings.Center-Location"))
        }
        set(value) {
            this.getConfig().set("Settings.Center-Location", LocationUtils.getStringFromLocation(value, false, true))
            this.reloadConfig()
        }

}