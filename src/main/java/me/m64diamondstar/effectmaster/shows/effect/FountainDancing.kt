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
import kotlin.random.Random

class FountainDancing : Effect() {

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

            // Doesn't need to play the show if it can't be viewed
            if(!location.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (section.get("Block") != null) Material.valueOf(
                section.getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Fountain with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            var blockData = if(section.get("BlockData") != null)
                Bukkit.createBlockData(material, section.getString("BlockData")!!) else material.createBlockData()
            val sequencer = LocationUtils.getTripleSequencerValues(if(section.getString("Sequencer") != null) section.getString("Sequencer")!! else "0:0.0,0.75,0.0;") ?: LocationUtils.getTripleSequencerValues("0:0.0,0.75,0.0;")!!
            val duration = if (section.get("Duration") != null) section.getInt("Duration") else {
                if (section.get("Length") != null) section.getInt("Length") else 20
            }
            val amount = if (section.get("Amount") != null) section.getInt("Amount") else 1
            val randomizer =
                if (section.get("Randomizer") != null) section.getDouble("Randomizer") / 10 else 0.0

            val brightness = if (section.get("Brightness") != null) section.getInt("Brightness") else -1

            val rotate = if (section.get("Rotate") != null) section.getBoolean("Rotate") else false
            val rotateSpeed = if (section.get("RotateSpeed") != null) section.getDouble("RotateSpeed").toFloat() else 1.0f

            var c = 0
            effectShow.runTimer(id, { task ->
                if (c == duration) {
                    task.cancel()
                    return@runTimer
                }

                repeat(amount) {
                    val (width, height, depth) = interpolateKeyframes(sequencer.toTriple()!!, c)
                    if(sequencer[c]?.fourth != null){
                        blockData = Bukkit.createBlockData(sequencer[c]?.fourth!!)
                    }
                    val velocity = Vector(
                        width + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer,
                        height + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3,
                        depth + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer
                    )

                    // Spawn falling block
                    emFallingBlock(blockData, location, velocity, brightness, rotate, rotateSpeed, players)
                }
                c++
            }, 0L, 1L)
        } catch (ex: Exception){
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

    override fun getIdentifier(): String {
        return "FOUNTAIN_DANCING"
    }

    override fun getDisplayMaterial(): Material {
        return Material.BLUE_SHULKER_BOX
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
            "Sequencer",
            "0: 0.0, 0.0, 0.0; 25: 0.0, 0.75, 0.0; 50: 0.3, 0.75, 0.0; 75: 0.0, 0.75, 0.0; 100: -0.3, 0.75, 0.0; 125: 0.0, 0.75, 0.0",
            "With the sequencer, you can edit the velocity of the fountain over time. " +
            "The first number is the time in ticks, the second is the velocity width, the third is the velocity height and the fourth is the velocity depth. (ticks:width,height,depth)" +
            "You can also add multiple values separated by semicolons. (ticks1:width1,height1,depth1; ticks2:width2,height2,depth2; ...)",
            {it},
            { LocationUtils.getTripleSequencerValues(it) != null &&LocationUtils.getTripleSequencerValues(it)!!.isNotEmpty() })
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