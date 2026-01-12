package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.ConditionalParameter
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.shows.utils.emFallingBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class FallingBlock : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val section = getSection(effectShow, id)
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(section.getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(section.getString("Location")!!) ?: return
            val material = if (section.get("Block") != null) Material.valueOf(
                section.getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(section.get("BlockData") != null)
                Bukkit.createBlockData(material, section.getString("BlockData")!!) else material.createBlockData()
            val velocity =
                if (section.get("Velocity") != null)
                    if (LocationUtils.getVectorFromString(section.getString("Velocity")!!) != null)
                        LocationUtils.getVectorFromString(section.getString("Velocity")!!)!!
                    else Vector(0.0, 0.0, 0.0)
                else Vector(0.0, 0.0, 0.0)

            val brightness = if (section.get("Brightness") != null) section.getInt("Brightness") else -1

            val rotate = if (section.get("Rotate") != null) section.getBoolean("Rotate") else false
            val rotateSpeed = if (section.get("RotateSpeed") != null) section.getDouble("RotateSpeed").toFloat() else 1.0f

            // Spawn falling block
            emFallingBlock(blockData, location, velocity, brightness, rotate, rotateSpeed, players)

        } catch (ex: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Falling Block with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Reason: ${ex.message}")
        }
    }

    override fun getIdentifier(): String {
        return "FALLING_BLOCK"
    }

    override fun getDisplayMaterial(): Material {
        return Material.SAND
    }

    override fun getDescription(): String {
        return "Spawns a falling block with customizable velocity."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(Parameter(
            "Location",
            "world, 0, 0, 0",
            DefaultDescriptions.LOCATION,
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "Velocity",
            "0, 0, 0",
            "The direction in which the falling block will get launched.",
            {it},
            { LocationUtils.getVectorFromString(it) != null })
        )
        list.add(SuggestingParameter(
            "Block",
            "STONE",
            DefaultDescriptions.BLOCK,
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } },
            Material.entries.filter { it.isBlock }.map { it.name.lowercase() })
        )
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, {it}, { true }))
        list.add(Parameter(
            "Brightness",
            -1,
            "The brightness of the block. Set it to -1 to use natural lighting.",
            { it.toInt() },
            { it.toIntOrNull() != null && it.toInt() in -1..15})
        )
        list.add(Parameter(
            "Rotate",
            false,
            "Whether the falling block should automatically rotate.",
            { it.toBoolean() },
            { it.toBooleanStrictOrNull() != null})
        )
        list.add(ConditionalParameter(
            "RotateSpeed",
            1f,
            "The multiplier for the rotational speed. When set to 1, it rotates at a random rate between -0.1 and 0.1 rad/tick.",
            { it.toFloat() },
            { it.toFloatOrNull() != null},
            { it.any { parameter -> parameter.key.name == "Rotate" && parameter.value == "true" } })
        )
        list.add(Parameter(
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}