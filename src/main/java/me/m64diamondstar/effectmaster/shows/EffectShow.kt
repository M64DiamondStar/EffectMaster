package me.m64diamondstar.effectmaster.shows

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.data.DataConfiguration
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

/**
 * Play custom effect shows.
 * @param category The category the show is located in.
 * @param name The name of the show.
 */
class EffectShow(private val category: String, private var name: String) {

    private var cancelled = false
    private val config = EffectShowConfig(category, name)

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
        header.add("https://effectmaster.m64.dev/")
        header.add("-----------------------------------------")

        config.getConfig().options().setHeader(header)

        config.getConfig().set("Settings.Looping", false)
        config.getConfig().set("Settings.Looping-Delay", 0)
        config.getConfig().set("Settings.Looping-Interval", 200)

        config.save()
    }

    fun deleteShow(){
        config.deleteFile()
    }

    fun rename(newName: String) {
        name = newName
        config.rename(newName)
    }

    fun saveConfig() = config.save()

    fun reloadConfig() = config.reload()

    fun deleteEffect(id: Int){
        val keys = config.getConfig().getKeys(false).toMutableList()
        keys.remove("Settings")

        config.reload()

        for(i in id until keys.size) {
            val currentSection = config.getConfig().getConfigurationSection("$i")
            val nextSection = config.getConfig().getConfigurationSection("${i + 1}")

            currentSection?.getValues(true)?.forEach{
                currentSection.set(it.key, null)
            }

            nextSection?.getValues(true)?.forEach{
                currentSection?.set(it.key,it.value)
            }
        }
        config.getConfig().set("${keys.size}", null)
        config.save()
    }

    fun getSection(path: String): ConfigurationSection? {
        config.reload()
        return config.getConfig().getConfigurationSection(path)
    }

    fun getMaxId(): Int {
        config.reload()
        var i = 1
        while (config.getConfig().getConfigurationSection("$i") != null) {
            i++
        }
        return i - 1
    }

    /**
     * Cancel this show instance.
     */
    fun cancel(){
        cancelled = true
    }

    /**
     * @return whether this show is cancelled or not.
     */
    fun isCancelled(): Boolean{
        return cancelled
    }

    /**
     * Plays the full show.
     * @param players The players that should see the show. If null, all online players will see it.
     */
    fun play(players: List<Player>?) {
        play(players, null)
    }

    /**
     * Plays the full show.
     */
    fun play(){
        play(null)
    }

    fun play(players: List<Player>?, at: Location?, reverse: Boolean = false){
        val settings = HashSet<ShowSetting>()
        if(at != null) settings.add(ShowSetting(ShowSetting.Identifier.PLAY_AT, at))

        config.reload()

        ShowUtils.addRunningShow(category, name, this)

        var count = 0L
        var tasksDone = 0

        EffectMaster.getFoliaLib().scheduler.runTimer( { task ->
            if(tasksDone >= getMaxId() || isCancelled()){
                ShowUtils.removeRunningShow(category, name, this)
                task.cancel()
                return@runTimer
            }

            var i = 1
            while (config.getConfig().getConfigurationSection("$i") != null) {
                if (config.getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
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
        config.reload()

        if(config.getConfig().getConfigurationSection("$id") == null) return false
        ShowUtils.addRunningShow(category, name, this)
        var count = 0L
        var tasksDone = 0
        EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
            if(tasksDone >= getMaxId() || isCancelled()){
                ShowUtils.removeRunningShow(category, name, this)
                task.cancel()
                return@runTimer
            }

            var i = id
            while (config.getConfig().getConfigurationSection("$i") != null) {
                if (config.getConfig().getConfigurationSection("$i")!!.getLong("Delay") == count) {
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
        config.reload()

        if(config.getConfig().getConfigurationSection("$id") == null) return false
        getEffect(id)?.execute(players, this, id)
        return true
    }

    fun getCategory(): String{
        return category
    }

    fun getName(): String{
        return name.replace(".yml", "")
    }

    fun setDefaults(id: Int, defaults: List<ParameterLike>){
        config.reload()
        for(pair in defaults){
            config.getConfig().set("$id.${pair.name}", pair.defaultValue)
        }
        config.save()
    }

    fun getAllEffects(): HashMap<Int, Effect?>{
        val map = HashMap<Int, Effect?>()
        for(id in 1..getMaxId()){
            map[id] = getEffect(id)
        }
        return map
    }

    fun getEffect(id: Int): Effect? {
        config.reload()
        return try{
            Effect.Type.getEffect(config.getConfig().getString("$id.Type")!!.uppercase())
        }catch (_: Exception){
            null
        }
    }

    var looping: Boolean
        get() {
            config.reload()
            return config.getConfig().getBoolean("Settings.Looping")
        }
        set(value) {
            config.reload()
            config.getConfig().set("Settings.Looping", value)
            config.save()
        }

    var loopingDelay: Long
        get() {
            config.reload()
            return config.getConfig().getLong("Settings.Looping-Delay")
        }
        set(value) {
            config.reload()
            config.getConfig().set("Settings.Looping-Delay", value)
            config.save()
        }

    var loopingInterval: Long
        get() {
            config.reload()
            return config.getConfig().getLong("Settings.Looping-Interval")
        }
        set(value) {
            config.reload()
            config.getConfig().set("Settings.Looping-Interval", value)
            config.save()
        }

    var centerLocation: Location?
        get() {
            config.reload()
            return LocationUtils.getLocationFromString(config.getConfig().getString("Settings.Center-Location"))
        }
        set(value) {
            config.reload()
            config.getConfig().set("Settings.Center-Location", LocationUtils.getStringFromLocation(value, false, true))
            config.save()
        }
}

private class EffectShowConfig(category: String, name: String): DataConfiguration("shows/$category", name)