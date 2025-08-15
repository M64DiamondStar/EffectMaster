package me.m64diamondstar.effectmaster.data

import org.bukkit.configuration.file.YamlConfiguration

object LoadedFiles {

    private val map: MutableMap<String, YamlConfiguration> = HashMap()

    fun isFileLoaded(path: String): Boolean {
        return map.containsKey(path)
    }

    fun getFile(path: String): YamlConfiguration? {
        return map[path]
    }

    fun loadFile(path: String, config: YamlConfiguration) {
        map[path] = config
    }

    fun unloadFile(path: String) {
        map.remove(path)
    }

}