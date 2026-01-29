package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.calculatePolygonalChain
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.parameter.ConditionalParameter
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.max

class FountainLine : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val section = getSection(effectShow, id)

            val fromLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(section.getString("FromLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(section.getString("FromLocation")!!) ?: return
            val toLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(section.getString("ToLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(section.getString("ToLocation")!!) ?: return

            // Doesn't need to play the show if it can't be viewed
            if(!fromLocation.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (section.get("Block") != null) Material.valueOf(
                section.getString("Block")!!.uppercase()
            ) else Material.STONE

            if(!material.isBlock) {
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
            val randomizer =
                if (section.get("Randomizer") != null) section.getDouble("Randomizer") / 10 else 0.0

            val brightness = if (section.get("Brightness") != null) section.getInt("Brightness") else -1

            val rotate = if (section.get("Rotate") != null) section.getBoolean("Rotate") else false
            val rotateSpeed = if (section.get("RotateSpeed") != null) section.getDouble("RotateSpeed").toFloat() else 1.0f

            val speed = if (section.get("Speed") != null) section.getDouble("Speed") * 0.05 else 0.05
            val amount = if (section.get("Amount") != null) section.getInt("Amount") else 1
            val frequency = if (section.get("Frequency") != null) section.getInt("Frequency") else 5

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The speed has to be greater than 0!")
                return
            }

            val distance = fromLocation.distance(toLocation)

            val dX: Double = (toLocation.x - fromLocation.x) / speed
            val dY: Double = (toLocation.y - fromLocation.y) / speed
            val dZ: Double = (toLocation.z - fromLocation.z) / speed

            // How long the effect is expected to last.
            val duration = max(max(dX.absoluteValue, dY.absoluteValue), dZ.absoluteValue)

            var c = 0
            effectShow.runTimer(id, { task ->
                if (c >= duration) {
                    task.cancel()
                    return@runTimer
                }

                repeat(amount) {
                    /* duration / distance = how many entities per block?
                if this is smaller than the frequency it has to spawn more entities in one tick

                The frequency / entities per block = how many entities per tick*/
                    if (duration / distance < frequency) {
                        val entitiesPerTick = frequency / (duration / distance)

                        repeat(entitiesPerTick.toInt()) { i2 ->
                            val subProgress = ((c + i2.toDouble() / entitiesPerTick) / duration).coerceAtMost(1.0)
                            val interpolatedLocation = calculatePolygonalChain(listOf(fromLocation, toLocation), subProgress)

                            // Spawn falling block
                            emFallingBlock(
                                blockData,
                                interpolatedLocation,
                                velocity.applyRandomizer(randomizer),
                                brightness,
                                rotate,
                                rotateSpeed,
                                players
                            )
                        }
                    }

                    /* The amount of entities per block is bigger than the frequency
                    => No need to spawn extra entities
                 */
                    else {
                        val progress = c.toDouble() / duration
                        val interpolatedLocation = calculatePolygonalChain(listOf(fromLocation, toLocation), progress)

                        // Spawn falling block
                        emFallingBlock(
                            blockData,
                            interpolatedLocation,
                            velocity.applyRandomizer(randomizer),
                            brightness,
                            rotate,
                            rotateSpeed,
                            players
                        )
                    }
                }

                c++
            }, 0L, 1L)
        }catch (ex: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Line with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Reason: ${ex.message}")
        }
    }

    override fun getIdentifier(): String {
        return "FOUNTAIN_LINE"
    }

    override fun getDisplayMaterial(): Material {
        return Material.LIGHT_BLUE_CONCRETE
    }

    override fun getDescription(): String {
        return "Spawns a fountain line of falling blocks with customizable velocity."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(Parameter(
            "FromLocation",
            "world, 0, 0, 0",
            "The start location of the fountain in the format of \"world, x, y, z\".",
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "ToLocation",
            "world, 1, 1, 1",
            "The end location of the fountain in the format of \"world, x, y, z\".",
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "Velocity",
            "0, 0, 0",
            DefaultDescriptions.VELOCITY,
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
            "Randomizer",
            0.0,
            "This randomizes the value of the velocity a bit. The higher the value, the more the velocity changes. It's best keeping this between 0 and 1.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        )
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
            "Amount",
            1,
            "The amount of blocks to spawn each tick. This has no effect on the frequency parameter.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Speed",
            1,
            "The speed of the fountain line progression. Measured in blocks/second.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null && it.toDouble() >= 0 })
        )
        list.add(Parameter(
            "Frequency",
            5,
            "In Minecraft a new entity or particle spawns every tick, but when the speed is very high an empty space comes between two entities or particles. To fix that you can use the frequency parameter. The frequency is how many entities/particles there should be every block. This effect only activates when the speed is too big that the amount of entities or particles per block is lower than the frequency.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
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