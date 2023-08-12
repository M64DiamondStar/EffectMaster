package me.m64diamondstar.effectmaster

import me.m64diamondstar.effectmaster.commands.EffectMasterCommand
import me.m64diamondstar.effectmaster.commands.EffectMasterTabCompleter
import me.m64diamondstar.effectmaster.commands.utils.SubCommandRegistry
import me.m64diamondstar.effectmaster.editor.listeners.ChatListener
import me.m64diamondstar.effectmaster.editor.listeners.LeaveListener
import me.m64diamondstar.effectmaster.shows.listeners.EntityChangeBlockListener
import me.m64diamondstar.effectmaster.shows.listeners.ItemMergeListener
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.traincarts.SignRegistry
import me.m64diamondstar.effectmaster.utils.gui.GuiListener
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.java.JavaPlugin

class EffectMaster : JavaPlugin() {

    companion object {
        lateinit var plugin: EffectMaster
        var isTrainCartsLoaded: Boolean = false
        var isAnimatronicsLoaded: Boolean = false
        var isProtocolLibLoaded: Boolean = false

        fun shortServerVersion(): Int {
            return plugin.server.version.split(".")[1].replace(Regex("[^0-9]"), "").toInt()
        }
    }

    override fun onEnable() {

        plugin = this // Initialize plugin var

        // Load config.yml
        saveDefaultConfig()

        // Load listeners
        this.server.pluginManager.registerEvents(EntityChangeBlockListener(), this)
        this.server.pluginManager.registerEvents(ItemMergeListener(), this)
        this.server.pluginManager.registerEvents(GuiListener(), this)
        this.server.pluginManager.registerEvents(ChatListener(), this)
        this.server.pluginManager.registerEvents(LeaveListener(), this)

        // Load commands
        this.getCommand("effectmaster")?.setExecutor(EffectMasterCommand())
        this.getCommand("effectmaster")?.tabCompleter = EffectMasterTabCompleter()

        SubCommandRegistry.loadSubCommands()

        // Load version
        this.logger.info("Detected server version ${this.server.version}. Going with short version ${shortServerVersion()}")

        // Try to load dependencies
        loadDependencies()

        // Try to register TrainCarts signs (does nothing if plugin isn't loaded)
        if(isTrainCartsLoaded) {
            SignRegistry.registerSigns()
        }

        // Enable bStats
        Metrics(this, 17340)

        // Check if there is a new update
        if(config.getBoolean("notify-updates")) {
            UpdateChecker(this, 107260).getVersion { version: String? ->
                if (description.version == version) {
                    logger.info("You're running the latest version.")
                } else {
                    logger.info("There is a new update available. You are running ${this.description.version}, the new version is $version")
                    logger.info("Please download the new version here: ")
                    logger.info("https://www.spigotmc.org/resources/effectmaster-create-beautiful-shows-in-your-server.107260/")
                }
            }
        }
    }

    override fun onDisable() {
        if(isTrainCartsLoaded) {
            SignRegistry.unregisterSigns()
        }

        ShowUtils.getFallingBlocks().forEach {
            it.remove()
        }

        ShowUtils.getDroppedItems().forEach {
            it.remove()
        }

        this.getCommand("effectmaster")?.unregister(SimpleCommandMap(this.server))
    }

    private fun loadDependencies(){
        if(this.server.pluginManager.getPlugin("Train_Carts") != null) {
            isTrainCartsLoaded = true
            this.logger.info("Train Carts found.")
        }else{
            this.logger.info("Train Carts not found, continuing without it.")
        }

        if(this.server.pluginManager.getPlugin("Animatronics") != null){
            isAnimatronicsLoaded = true
            this.logger.info("Animatronics found.")
        }else{
            this.logger.info("Animatronics not found, continuing without it.")
        }

        if(this.server.pluginManager.getPlugin("ProtocolLib") != null){
            isProtocolLibLoaded = true
            this.logger.info("ProtocolLib found.")
        }else{
            this.logger.info("ProtocolLib not found, continuing without it.")
        }
    }

}