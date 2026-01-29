package me.m64diamondstar.effectmaster.shows

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.data.DataConfiguration
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.EffectShowTaskException
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.function.Consumer

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

    /**
     * Saves the configuration file.
     */
    fun saveConfig() = config.save()

    /**
     * Reloads the configuration file.
     */
    fun reloadConfig() = config.reload()

    /**
     * @return the raw configuration as a string.
     */
    fun rawConfig(): String = config.file.readText()

    /**
     * Imports the configuration file from raw text.
     * USE WITH CAUTION: this clears the old show file!
     * @param rawContent the content as a String.
     */
    fun import(rawContent: String) {
        config.file.writeText(rawContent)
        config.reload()
    }

    fun getDelay(id: Int): Long {
        return config.getConfig().getConfigurationSection("$id")?.getLong("Delay") ?: 0
    }

    fun deleteEffect(id: Int) {
        val keys = config.getConfig().getKeys(false).toMutableList()
        keys.remove("Settings")

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
        return config.getConfig().getConfigurationSection(path)
    }

    fun getMaxId(): Int {
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

    fun play(players: List<Player>?, at: Location?){
        val settings = HashSet<ShowSetting>()
        if(at != null) settings.add(ShowSetting(ShowSetting.Identifier.PLAY_AT, at))

        config.reload()

        ShowUtils.addRunningShow(category, name, this)

        var count = 0L
        var tasksDone = 0

        Bukkit.getGlobalRegionScheduler().runAtFixedRate(EffectMaster.plugin(), { task ->
            if(tasksDone >= getMaxId() || isCancelled()){
                ShowUtils.removeRunningShow(category, name, this)
                task.cancel()
                return@runAtFixedRate
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
        }, 1L, 1L)
    }

    /**
     * Starts the show from a specific moment.
     * @param id the ID from where to start the show.
     * @return Whether the show was started successfully.
     */
    fun playFrom(id: Int, players: List<Player>?): Boolean{
        if(config.getConfig().getConfigurationSection("$id") == null) return false
        ShowUtils.addRunningShow(category, name, this)
        var count = 0L
        var tasksDone = 0
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(EffectMaster.plugin(), { task ->
            if(tasksDone >= getMaxId() || isCancelled()){
                ShowUtils.removeRunningShow(category, name, this)
                task.cancel()
                return@runAtFixedRate
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
        }, 1L, 1L)
        return true
    }

    /**
     * Only plays the effect with the given id.
     * @param id the ID of the effect that should be played.
     */
    fun playOnly(id: Int, players: List<Player>?): Boolean{
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
        for(parameterLike in defaults){
            val defaultValue = parameterLike.defaultValue

            config.getConfig().set(
                "$id.${parameterLike.name}",
                if(defaultValue is String) // Convert types if necessary
                    parameterLike.parameterTypeConverter.getAsType(defaultValue)
                else
                    defaultValue)
        }
        config.save()
    }

    fun setDefault(id: Int, default: ParameterLike){
        val defaultValue = default.defaultValue

        config.getConfig().set(
            "$id.${default.name}",
            if(defaultValue is String) // Convert types if necessary
                default.parameterTypeConverter.getAsType(defaultValue)
            else
                defaultValue)

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
        return try{
            Effect.Type.getEffect(config.getConfig().getString("$id.Type")!!.uppercase())
        }catch (_: Exception){
            null
        }
    }

    /**
     * Run a task synchronously
     */
    fun runTask(effectId: Int, task: Consumer<ScheduledTask>) {
        Bukkit.getGlobalRegionScheduler().run(EffectMaster.plugin(), { scheduledTask ->
            try{
                task.accept(scheduledTask)
            } catch (t: Throwable) {
                scheduledTask.cancel()
                throw EffectShowTaskException(
                    "Exception while running EffectShow task for $category/$name. Error while executing ID $effectId.",
                    t
                )
            }
        })
    }

    /**
     * Run a delayed task
     * @param delay must be equal to or greater than 1
     */
    fun runLater(effectId: Int, task: Consumer<ScheduledTask>, delay: Long) {
        Bukkit.getGlobalRegionScheduler().runDelayed(EffectMaster.plugin(), { scheduledTask ->
            try{
                task.accept(scheduledTask)
            } catch (t: Throwable) {
                scheduledTask.cancel()
                throw EffectShowTaskException(
                    "Exception while running delayed EffectShow task for $category/$name. Error while executing ID $effectId.",
                    t
                )
            }
        }, delay)
    }

    /**
     * Run a task timer with a given delay and period
     * @param delay must be equal to or greater than 1
     * @param period how often the task will be executed in ticks, must be equal or greater than 1
     */
    fun runTimer(effectId: Int, task: Consumer<ScheduledTask>, delay: Long, period: Long) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(EffectMaster.plugin(), { scheduledTask ->
            try{
                task.accept(scheduledTask)
            } catch (t: Throwable) {
                scheduledTask.cancel()
                throw EffectShowTaskException(
                    "Exception while running EffectShow timer for $category/$name. Error while executing ID $effectId.",
                    t
                )
            }
        }, delay, period)
    }

    var looping: Boolean
        get() {
            return config.getConfig().getBoolean("Settings.Looping")
        }
        set(value) {
            config.getConfig().set("Settings.Looping", value)
            config.save()

            if (value)
                ShowLooper.loop(this)
            else
                ShowLooper.stopLoop(this)
        }

    var loopingDelay: Long
        get() {
            return config.getConfig().getLong("Settings.Looping-Delay")
        }
        set(value) {
            config.getConfig().set("Settings.Looping-Delay", value)
            config.save()
            ShowLooper.updateLoop(this)
        }

    var loopingInterval: Long
        get() {
            return config.getConfig().getLong("Settings.Looping-Interval")
        }
        set(value) {
            config.getConfig().set("Settings.Looping-Interval", value)
            config.save()
            ShowLooper.updateLoop(this)
        }

    var centerLocation: Location?
        get() {
            return LocationUtils.getLocationFromString(config.getConfig().getString("Settings.Center-Location"))
        }
        set(value) {
            config.getConfig().set("Settings.Center-Location", LocationUtils.getStringFromLocation(value,
                asBlock = false,
                withWorld = true
            ))
            config.save()
        }
}

private class EffectShowConfig(category: String, name: String): DataConfiguration("shows/$category", name)