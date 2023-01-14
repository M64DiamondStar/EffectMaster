package me.m64diamondstar.effectmaster

import me.m64diamondstar.effectmaster.commands.EffectMasterCommand
import me.m64diamondstar.effectmaster.commands.EffectMasterTabCompleter
import me.m64diamondstar.effectmaster.commands.utils.SubCommandRegistry
import me.m64diamondstar.effectmaster.shows.listeners.EntityChangeBlockListener
import me.m64diamondstar.effectmaster.traincarts.SignRegistry
import me.m64diamondstar.effectmaster.utils.gui.GuiListener
import org.bukkit.plugin.java.JavaPlugin

class EffectMaster : JavaPlugin() {

    companion object {
        lateinit var plugin: EffectMaster
        var isTrainCartsLoaded: Boolean = false
        var isAnimatronicsLoaded: Boolean = false

        fun shortServerVersion(): Int {
            return plugin.server.version.split(".")[1].toInt()
        }
    }

    override fun onEnable() {
        plugin = this // Initialize plugin var

        // Load listeners
        this.server.pluginManager.registerEvents(EntityChangeBlockListener(), this)
        this.server.pluginManager.registerEvents(GuiListener(), this)

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

        Metrics(this, 17340)

    }

    override fun onDisable() {
        if(isTrainCartsLoaded) {
            SignRegistry.unregisterSigns()
        }
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
    }

}