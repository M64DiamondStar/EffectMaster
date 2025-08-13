package me.m64diamondstar.effectmaster.extensions

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.utils.severeLog
import me.m64diamondstar.effectmaster.utils.warningLog
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile

object ExtensionManager {

    private val loadedExtensions = mutableMapOf<String, LoadedExtension>()

    fun loadExtensions() {
        val root = File(EffectMaster.plugin().dataFolder, "extensions").toPath()

        Files.createDirectories(root)
        Files.list(root).use { stream ->
            stream.filter { it.toString().endsWith(".jar") }.forEach { jarPath ->
                val jarFile = JarFile(jarPath.toFile())
                val entry = jarFile.getEntry("effectmaster-extension.yml") ?: run {
                    warningLog("No manifest in ${jarPath.fileName}")
                    return@forEach
                }
                val meta = loadExtensionMeta(jarPath) ?: run {
                    warningLog("Missing effectmaster-extension.yml in ${jarPath.fileName}")
                    return@forEach
                }

                // Required extension settings
                val name = meta.getString("name")
                if(name == null){
                    severeLog("Name of extension ${jarFile.name} is missing! Skipping this extension...")
                    return@forEach
                }

                val mainClass = meta.getString("main")
                if(mainClass == null){
                    severeLog("Main class of extension ${jarFile.name} is missing! Skipping this extension...")
                    return@forEach
                }

                val version = meta.getString("version")
                if(version == null){
                    severeLog("Version of extension ${jarFile.name} is missing! Skipping this extension...")
                    return@forEach
                }

                // Optional extension settings
                val authors = meta.getStringList("authors")

                val loadedExtension = LoadedExtension(
                    name = name,
                    mainClass = mainClass,
                    version = version,
                    authors = authors
                    )

            }
        }
    }


    fun loadExtensionMeta(jarPath: Path): YamlConfiguration? {
        JarFile(jarPath.toFile()).use { jar ->
            val entry = jar.getJarEntry("effectmaster-extension.yml") ?: return null
            jar.getInputStream(entry).use { input ->
                val config = YamlConfiguration()
                config.load(input.reader()) // Bukkit's loader
                return config
            }
        }
    }


}