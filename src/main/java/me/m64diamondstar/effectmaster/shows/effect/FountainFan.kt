package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.ktx.toTriple
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
import kotlin.collections.any
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import kotlin.text.equals
import kotlin.text.toBoolean
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toDouble
import kotlin.text.toDoubleOrNull
import kotlin.text.toFloat
import kotlin.text.toFloatOrNull
import kotlin.text.toInt
import kotlin.text.toIntOrNull
import kotlin.text.toLong
import kotlin.text.toLongOrNull
import kotlin.text.uppercase

class FountainFan : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        try {
            val section = getSection(effectShow, id)

            val location =
                if (settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }) {
                    LocationUtils.getRelativeLocationFromString(
                        section.getString("Location")!!,
                        effectShow.centerLocation ?: return
                    )?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                } else {
                    LocationUtils.getLocationFromString(section.getString("Location")!!) ?: return
                }

            if (!location.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (section.get("Block") != null)
                Material.valueOf(section.getString("Block")!!.uppercase())
            else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Fountain Fan with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            var blockData = if (section.get("BlockData") != null)
                Bukkit.createBlockData(material, section.getString("BlockData")!!)
            else material.createBlockData()

            val directionSequencer = LocationUtils.getTripleSequencerValues(
                section.getString("DirectionSequencer") ?: "0: 0.0, 0.75, 0.0;"
            ) ?: LocationUtils.getTripleSequencerValues("0: 0.0, 0.75, 0.0;")!!

            val rotationSequencer = LocationUtils.getRotationSequencerValues(
                section.getString("RotationSequencer") ?: "0: 0.0;"
            ) ?: LocationUtils.getRotationSequencerValues("0: 0.0;")!!

            val spreadSequencer = LocationUtils.getSpreadSequencerValues(
                section.getString("SpreadSequencer") ?: "0: 30.0, 6;"
            ) ?: LocationUtils.getSpreadSequencerValues("0: 30.0, 6;")!!

            val duration = if (section.get("Duration") != null) section.getInt("Duration") else 125

            val amount = if (section.get("Amount") != null) section.getInt("Amount") else 1
            val randomizer = if (section.get("Randomizer") != null) section.getDouble("Randomizer") / 10 else 0.0
            val brightness = if (section.get("Brightness") != null) section.getInt("Brightness") else -1
            val rotate = if (section.get("Rotate") != null) section.getBoolean("Rotate") else false
            val rotateSpeed = if (section.get("RotateSpeed") != null) section.getDouble("RotateSpeed").toFloat() else 1.0f

            var c = 0
            effectShow.runTimer(id, { task ->
                if (c == duration) {
                    task.cancel()
                    return@runTimer
                }

                // Interpolate all three sequencers for this tick
                val (baseWidth, baseHeight, baseDepth) = interpolateKeyframes(directionSequencer.toTriple()!!, c)
                if (directionSequencer[c]?.fourth != null) {
                    blockData = Bukkit.createBlockData(directionSequencer[c]?.fourth!!)
                }

                val yawDeg = interpolateRotation(rotationSequencer, c)
                val (arcAngle, jetCount) = interpolateSpread(spreadSequencer, c)

                // Spawn each jet of the fan
                val baseDir = Vector(baseWidth, baseHeight, baseDepth)
                val speed = baseDir.length()

                if (speed > 0.0) {
                    val dirUnit = baseDir.clone().normalize()

                    // Build a spread axis perpendicular to the direction.
                    // This ensures the fan is always visible regardless of where the direction points.
                    val worldUp = Vector(0.0, 1.0, 0.0)
                    val rawSpreadAxis = if (abs(dirUnit.dot(worldUp)) < 0.999)
                        dirUnit.clone().crossProduct(worldUp).normalize()
                    else
                        dirUnit.clone().crossProduct(Vector(1.0, 0.0, 0.0)).normalize()

                    // Rotate the spread axis around the direction axis by the rotation sequencer value.
                    // This spins the fan like a wheel around its own pointing direction.
                    val spreadAxis = rodrigues(rawSpreadAxis, dirUnit, Math.toRadians(yawDeg))

                    repeat(jetCount) { i ->
                        val jetOffsetDeg = if (jetCount == 1) 0.0
                        else -arcAngle / 2.0 + i * arcAngle / (jetCount - 1)

                        // Rotate the center direction around the spread axis by the jet's offset
                        val jetDir = rodrigues(dirUnit, spreadAxis, Math.toRadians(jetOffsetDeg))

                        repeat(amount) {
                            val velocity = Vector(
                                jetDir.x * speed + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer,
                                jetDir.y * speed + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3,
                                jetDir.z * speed + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer
                            )
                            emFallingBlock(blockData, location, velocity, brightness, rotate, rotateSpeed, players)
                        }
                    }
                }

                c++
            }, 1L, 1L)

        } catch (ex: Exception) {
            EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Reason: ${ex.message}")
        }
    }

    private fun interpolateKeyframes(sequencer: Map<Int, Triple<Double?, Double?, Double?>>, tick: Int): Triple<Double, Double, Double> {
        val keys = sequencer.keys.sorted()

        // Helper function to interpolate a single value (width or height)
        fun interpolateFor(tick: Int, selector: (Triple<Double?, Double?, Double?>) -> Double?): Double {
            val (prevKey, nextKey) = findKeyframes(keys, tick) { key ->
                sequencer[key]?.let(selector) != null
            }

            val prevValue = sequencer[prevKey]?.let(selector) ?: 0.0
            val nextValue = sequencer[nextKey]?.let(selector) ?: prevValue

            if (prevKey == nextKey) {
                return prevValue
            }

            val t = (tick - prevKey).toDouble() / (nextKey - prevKey)
            return prevValue + t * (nextValue - prevValue)
        }

        val width = interpolateFor(tick) { it.first }
        val height = interpolateFor(tick) { it.second }
        val depth = interpolateFor(tick) { it.third }

        return Triple(width, height, depth)
    }


    private fun findKeyframes(keys: List<Int>, tick: Int, predicate: (Int) -> Boolean): Pair<Int, Int> {
        var prevKey = keys.firstOrNull(predicate) ?: keys.first()
        var nextKey = keys.lastOrNull(predicate) ?: keys.last()

        for (key in keys.filter(predicate)) {
            if (key <= tick) {
                prevKey = key
            }
            if (key >= tick) {
                nextKey = key
                break
            }
        }

        return Pair(prevKey, nextKey)
    }

    private fun interpolateRotation(sequencer: Map<Int, Double?>, tick: Int): Double {
        val keys = sequencer.keys.sorted()
        val prevKey = keys.lastOrNull { it <= tick } ?: keys.first()
        val nextKey = keys.firstOrNull { it >= tick } ?: keys.last()

        val prevValue = sequencer[prevKey] ?: 0.0
        val nextValue = sequencer[nextKey] ?: prevValue

        if (prevKey == nextKey) return prevValue

        val t = (tick - prevKey).toDouble() / (nextKey - prevKey)
        return prevValue + t * (nextValue - prevValue)
    }

    private fun interpolateSpread(sequencer: Map<Int, Pair<Double?, Int?>>, tick: Int): Pair<Double, Int> {
        val keys = sequencer.keys.sorted()
        val prevKey = keys.lastOrNull { it <= tick } ?: keys.first()
        val nextKey = keys.firstOrNull { it >= tick } ?: keys.last()

        val prev = sequencer[prevKey] ?: Pair(30.0, 1)
        val next = sequencer[nextKey] ?: prev

        if (prevKey == nextKey)
            return Pair(prev.first ?: 30.0, (prev.second ?: 1).coerceAtLeast(1))

        val t = (tick - prevKey).toDouble() / (nextKey - prevKey)
        val angle = (prev.first ?: 30.0) + t * ((next.first ?: prev.first ?: 30.0) - (prev.first ?: 30.0))
        val jetCount = ((prev.second ?: 1) + t * ((next.second ?: prev.second ?: 1) - (prev.second ?: 1))).roundToInt()
            .coerceAtLeast(1)

        return Pair(angle, jetCount)
    }

    /**
     * Rotates vector [v] around unit axis [k] by [theta] radians using Rodrigues' rotation formula:
     * v_rot = v·cos(θ) + (k×v)·sin(θ) + k·(k·v)·(1−cos(θ))
     */
    private fun rodrigues(v: Vector, k: Vector, theta: Double): Vector {
        val cosT = cos(theta)
        val sinT = sin(theta)
        return v.clone().multiply(cosT)
            .add(k.clone().crossProduct(v).multiply(sinT))
            .add(k.clone().multiply(k.dot(v) * (1 - cosT)))
    }

    override fun getIdentifier(): String {
        return "FOUNTAIN_FAN"
    }

    override fun getDisplayMaterial(): Material {
        return Material.TUBE_CORAL
    }

    override fun getDescription(): String {
        return "Spawn a fountain of falling blocks which you can move with the Sequencer parameter."
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
            "DirectionSequencer",
            "0: 0.0, 0.0, 0.0; 25: 0.0, 0.75, 0.0; 50: 0.3, 0.75, 0.0; 75: 0.0, 0.75, 0.0; 100: -0.3, 0.75, 0.0; 125: 0.0, 0.75, 0.0",
            "With the direction sequencer, you can edit the velocity of the fountain over time. " +
                    "The first number is the time in ticks, the second is the velocity width, the third is the velocity height and the fourth is the velocity depth. (ticks:width,height,depth)" +
                    "You can also add multiple values separated by semicolons. (ticks1:width1,height1,depth1; ticks2:width2,height2,depth2; ...)",
            {it},
            { LocationUtils.getTripleSequencerValues(it) != null &&LocationUtils.getTripleSequencerValues(it)!!.isNotEmpty() })
        )
        list.add(Parameter(
            "RotationSequencer",
            "0: 0.0; 25: 90.0; 50: 180.0; 75: 270.0; 100: 360.0",
            "With the rotation sequencer, you can edit the rotation of the fountain over time. " +
                    "The first number is the time in ticks, the second is the rotation in degrees. " +
                    "You can also add multiple values separated by semicolons. (ticks1:rotation1; ticks2:rotation2; ...)",
            {it},
            { LocationUtils.getRotationSequencerValues(it) != null &&LocationUtils.getRotationSequencerValues(it)!!.isNotEmpty() })
        )
        list.add(Parameter(
            "SpreadSequencer",
            "0: 30.0, 6;",
            "With the spread sequencer, you can edit the spread of the fountain over time. " +
                    "The first number is the total angle of the arc, so the arc between the first jet and the last jet. The second is the amount of jets there are. (ticks:arc angle,amount of jets)",
            {it},
            { LocationUtils.getSpreadSequencerValues(it) != null &&LocationUtils.getSpreadSequencerValues(it)!!.isNotEmpty() })
        )
        list.add(SuggestingParameter(
            "Block",
            "BLUE_STAINED_GLASS",
            DefaultDescriptions.BLOCK,
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } },
            Material.entries.filter { it.isBlock }.map { it.name.lowercase() })
        )
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, {it}, { true }))
        list.add(Parameter(
            "Duration",
            125,
            DefaultDescriptions.DURATION,
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
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
            "The amount of blocks to spawn each tick.",
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