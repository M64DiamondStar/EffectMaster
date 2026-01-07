package me.m64diamondstar.effectmaster.shows

import me.m64diamondstar.effectmaster.data.DataConfiguration
import org.bukkit.Material

class EffectPresets : DataConfiguration("", "effect_presets") {

    init {
        val header = listOf(
            "==========================================",
            "This is where all the effect presets are stored. You can edit",
            "them manually but it's easier to add or remove entries in-game",
            "=========================================="
        )

        this.getConfig().options().setHeader(header)
    }

    fun addPreset(preset: Preset) {
        val section = this.getConfig().createSection("${preset.effectType}.${preset.name}")
        section["material"] = preset.material.toString()
        val valuesSection = section.createSection("values")
        for ((key, value) in preset.values) {
            valuesSection[key] = value
        }
        this.save()
    }

    fun getPreset(effectType: String, name: String): Preset? {
        val section = this.getConfig().getConfigurationSection("$effectType.$name") ?: return null
        val materialString = section.getString("material") ?: "FILLED_MAP"
        val material = try {
            Material.valueOf(materialString)
        } catch (_: IllegalArgumentException) {
            Material.FILLED_MAP
        }
        val valuesSection = section.getConfigurationSection("values") ?: return null
        val values = valuesSection.getValues(false).map { it.key to it.value.toString() }
        val type = values.find { it.first.equals("Type") }?.second ?: return null
        return Preset(name, type, material, values)
    }

    fun getAllPresets(effectType: String): List<Preset> {
        val presets = mutableListOf<Preset>()
        val config = this.getConfig()
        val section = config.getConfigurationSection(effectType) ?: return emptyList()

        for (presetName in section.getKeys(false)) {
            val preset = getPreset(effectType, presetName)
            if (preset != null) {
                presets.add(preset)
            }
        }

        return presets
    }

    fun removePreset(effectType: String, name: String) {
        val config = this.getConfig()
        if (config.contains("$effectType.$name")) {
            config.set("$effectType.$name", null)
            this.save()
        }
    }

    fun rawConfig(effectType: String, name: String): String? {
        val section = this.getConfig().getConfigurationSection("$effectType.$name") ?: return null
        val tempConfig = org.bukkit.configuration.file.YamlConfiguration()
        tempConfig.set("$effectType.$name", section)
        return tempConfig.saveToString()
    }

    data class Preset(
        val name: String,
        val effectType: String,
        val material: Material,
        val values: List<Pair<String, String>>
    )

}