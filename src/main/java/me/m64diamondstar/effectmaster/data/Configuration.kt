package me.m64diamondstar.effectmaster.data

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.lang.Exception

abstract class Configuration (path: String, name: String) {

    private lateinit var config: FileConfiguration
    private var path: File
    private lateinit var file: File
    private var name: String

    /**
     * Constructor for making/changing a Config File
     */
    init {

        this.path = File(EffectMaster.plugin.dataFolder, path)
        this.name = name.replace(".yml", "")

        this.path.mkdirs()

        createConfig()

    }

    /**
     * Get FileConfiguration
     */
    fun getConfig(): FileConfiguration {
        return config
    }

    /**
     * Delete the File
     */
    fun deleteFile(){
        file.delete()
    }

    /**
     * Saves the file
     */
    private fun saveConfig() {
        if(!file.exists())
            createConfig()
        try{
            config.save(file)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * Reloads config
     * @return FileConfiguration
     */
    fun reloadConfig(): FileConfiguration {
        saveConfig()
        return config
    }

    /**
     * Check if the configuration file exists
     * @return Boolean
     */
    fun existsConfig(): Boolean{
        file = File(path, "$name.yml")
        return file.exists()
    }

    /**
     * Create file if it doesn't exist
     */
    private fun createConfig(){

        file = File(path, "$name.yml")

        if(!file.exists()) {

            file.createNewFile()
            config = YamlConfiguration()

            saveConfig()
        }else{
            config = YamlConfiguration.loadConfiguration(file)
        }

    }
}