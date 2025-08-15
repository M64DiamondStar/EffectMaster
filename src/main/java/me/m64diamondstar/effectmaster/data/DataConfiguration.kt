package me.m64diamondstar.effectmaster.data

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

abstract class DataConfiguration(private val path: String, name: String) {

    private var config: YamlConfiguration? = null
    val file: File by lazy { File(EffectMaster.plugin().dataFolder, "$path/${name.replace(".yml", "")}.yml") }

    init {
        file.parentFile.mkdirs()
        createConfig()
    }

    open fun deleteFile() {
        file.delete()
        LoadedFiles.unloadFile(file.path)
    }

    fun getConfig(): FileConfiguration {
        return config ?: throw IllegalStateException("Configuration is not initialized.")
    }

    /**
     * Rename the config file
     */
    fun rename(name: String){
        LoadedFiles.unloadFile(file.path)
        val source = Paths.get("$file")
        Files.move(source, source.resolveSibling("$name.yml"))
        LoadedFiles.loadFile(file.path, config!!)
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
        LoadedFiles.loadFile(file.path, config!!)
    }

    fun save() {
        createConfig()
        try {
            config?.save(file)
            LoadedFiles.loadFile(file.path, config!!)
            config = LoadedFiles.getFile(file.path)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun createConfig() {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        if(LoadedFiles.isFileLoaded(file.path))
            config = LoadedFiles.getFile(file.path)
        else
            reload()
    }
}
