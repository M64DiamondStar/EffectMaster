package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.Spline
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

class FountainPath : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val section = getSection(effectShow, id)

            val path =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativePathFromString(section.getString("Path")!!,
                        effectShow.centerLocation ?: return)
                        .map { it.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) }
                }else
                    LocationUtils.getLocationPathFromString(section.getString("Path")!!)
            if(path.size < 2) return

            // Doesn't need to play the show if it can't be viewed
            if(!path[0].chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
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
            val frequency = if (section.get("Frequency") != null) section.getInt("Frequency") else 5
            val amount = if (section.get("Amount") != null) section.getInt("Amount") else 1

            val splineType = if (section.get("SplineType") != null) Spline.valueOf(
                section.getString("SplineType")!!.uppercase()
            ) else Spline.CATMULL_ROM

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play Fountain Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The speed has to be greater than 0!")
                return
            }

            if(splineType == Spline.CATMULL_ROM && path.size < 4){
                EffectMaster.plugin().logger.warning("Couldn't play Block Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("You need at least 4 path locations with the CATMULL_ROM spline type.")
                return
            }

            var distance = 0.0
            for(loc in 1 until path.size){
                distance += path[loc - 1].distance(path[loc])
            }

            // How long the effect is expected to last.
            val duration = distance / speed

            var c = 0.0
            effectShow.runTimer(id, { task ->
                if (c >= 1) {
                    task.cancel()
                    return@runTimer
                }

                repeat(amount) {
                    /*
                duration / distance = how many entities per block?
                if this is smaller than the frequency it has to spawn more entities in one tick

                The frequency / entities per block = how many entities per tick
                */
                    if (duration / distance < frequency) {
                        val entitiesPerTick = frequency / (duration / distance)
                        for (i2 in 1..entitiesPerTick.toInt()) {
                            val progress = c + 1.0 / duration / entitiesPerTick * i2
                            if(progress > 1) continue

                            // Spawn falling block
                            emFallingBlock(blockData, splineType.calculate(
                                    path,
                                c + 1.0 / duration / entitiesPerTick * i2
                                ),
                                velocity.applyRandomizer(randomizer),
                                brightness,
                                rotate,
                                rotateSpeed,
                                players)
                        }
                    }

                    /*
                    The amount of entities per block is bigger than the frequency
                    => No need to spawn extra entities
                */
                    else {

                        // Spawn falling block
                        emFallingBlock(
                            blockData,
                            splineType.calculate(path, c),
                            velocity.applyRandomizer(randomizer),
                            brightness,
                            rotate,
                            rotateSpeed,
                            players
                        )
                    }
                }

                c += 1.0 / duration
            }, 1L, 1L)
        }catch (ex: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Reason: ${ex.message}")
        }
    }

    override fun getIdentifier(): String {
        return "FOUNTAIN_PATH"
    }

    override fun getDisplayMaterial(): Material {
        return Material.BLUE_CONCRETE
    }

    override fun getDescription(): String {
        return "Spawns a fountain path falling blocks with customizable velocity."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<ParameterLike> {
        val list = ArrayList<ParameterLike>()
        list.add(
            Parameter(
                "Path",
                "world, 0, 0, 0",
                "The path the origin of the fountain follows using the format of " +
                        "\"world, x1, y1, z1; x2, y2, z2; x3, y3, z3\". You can of course repeat this process as much as you would like. Use a ; to separate different locations.",
                { it },
                { LocationUtils.getLocationPathFromString(it).isNotEmpty() })
        )
        list.add(
            Parameter(
                "Velocity",
                "0, 0, 0",
                DefaultDescriptions.VELOCITY,
                { it },
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
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, { it }, { true }))
        list.add(
            Parameter(
                "Duration",
                1,
                DefaultDescriptions.DURATION,
                { it.toInt() },
                { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(
            Parameter(
                "Randomizer",
                0.0,
                "This randomizes the value of the velocity a bit. The higher the value, the more the velocity changes. It's best keeping this between 0 and 1.",
                { it.toDouble() },
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
        list.add(
            Parameter(
                "Amount",
                1,
                "The amount of blocks to spawn each tick. This has no effect on the frequency parameter.",
                { it.toInt() },
                { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(
            Parameter(
                "Speed",
                1,
                "The speed of the fountain path progression. Measured in blocks/second.",
                { it.toDouble() },
                { it.toDoubleOrNull() != null && it.toDouble() >= 0 })
        )
        list.add(
            Parameter(
                "Frequency",
                5,
                "In Minecraft a new entity or particle spawns every tick, but when the speed is very high an empty space comes between two entities or particles. To fix that you can use the frequency parameter. The frequency is how many entities/particles there should be every block. This effect only activates when the speed is too big that the amount of entities or particles per block is lower than the frequency.",
                { it.toInt() },
                { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(
            Parameter(
                "SplineType",
                "CATMULL_ROM",
                DefaultDescriptions.SPLINE_TYPE,
                { it.uppercase() },
                { Spline.entries.any { spline -> it.equals(spline.name, ignoreCase = true) } })
        )
        list.add(
            Parameter(
                "Delay",
                0,
                DefaultDescriptions.DELAY,
                { it.toInt() },
                { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}