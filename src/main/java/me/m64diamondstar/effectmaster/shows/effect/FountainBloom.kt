package me.m64diamondstar.effectmaster.shows.effect

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.parameter.Parameter
import me.m64diamondstar.effectmaster.shows.parameter.ParameterLike
import me.m64diamondstar.effectmaster.shows.parameter.SuggestingParameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.ktx.toPair
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FountainBloom : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return

            // Doesn't need to play the show if it can't be viewed
            if(!location.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
                getSection(effectShow, id).getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Fountain Bloom with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            var blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()

            val sequencer = LocationUtils.getDoubleSequencerValues(if(getSection(effectShow, id).getString("Sequencer") != null) getSection(effectShow, id).getString("Sequencer")!! else "0:0.5,0.5;") ?: LocationUtils.getDoubleSequencerValues("0:0.5,0.5;")!!

            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 15
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getInt("Duration") else 20
            val randomizer = if (getSection(effectShow, id).get("Randomizer") != null) getSection(effectShow, id).getDouble("Randomizer") / 10 else 0.0

            var c = 0
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->

                if (c == duration) {
                    task.cancel()
                    return@runTimer
                }

                val (width, height) = interpolateKeyframes(sequencer.toPair()!!, c)

                if(sequencer[c]?.third != null){
                    blockData = Bukkit.createBlockData(sequencer[c]?.third!!)
                }

                repeat(amount){
                    val fallingBlock = location.world!!.spawnFallingBlock(location, blockData)
                    fallingBlock.dropItem = false
                    fallingBlock.isPersistent = false
                    fallingBlock.persistentDataContainer.set(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"),
                        PersistentDataType.BOOLEAN, true)

                    val angle = it.toDouble() * (2 * Math.PI / amount.toDouble())
                    val x = cos(angle) * width + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer
                    val y = height + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3
                    val z = sin(angle) * width  + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer
                    fallingBlock.velocity = Vector(x, y, z)

                    ShowUtils.addFallingBlock(fallingBlock)

                    if (players != null && EffectMaster.isProtocolLibLoaded)
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (!players.contains(player)) {
                                val protocolManager = ProtocolLibrary.getProtocolManager()
                                val removePacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
                                removePacket.intLists.write(0, listOf(fallingBlock.entityId))
                                protocolManager.sendServerPacket(player, removePacket)
                            }
                        }
                }

                c++
            }, 0L, 1L)
        } catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
            EffectMaster.plugin().logger.warning("- The sequencer value is invalid")
        }
    }

    private fun interpolateKeyframes(sequencer: Map<Int, Pair<Double?, Double?>>, tick: Int): Pair<Double, Double> {
        val keys = sequencer.keys.sorted()

        // Helper function to interpolate a single value (width or height)
        fun interpolateFor(tick: Int, selector: (Pair<Double?, Double?>) -> Double?): Double {
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

        return Pair(width, height)
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
        return "FOUNTAIN_BLOOM"
    }

    override fun getDisplayMaterial(): Material {
        return Material.BLUE_WOOL
    }

    override fun getDescription(): String {
        return "Spawn a bloom-shaped fountain."
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
            "0: 0.1, 0.8; 50: 0.6, 0.8; 100: 0.1, 0.8",
            "With the sequencer, you can edit the velocity of the fountain over time. " +
                    "The first number is the time in ticks, the second is the velocity width and the third is the velocity height. (ticks:width,height)" +
                    "You can also add multiple values separated by semicolons. (ticks1:width1,height1; ticks2:width2,height2; ...)",
            {it},
            { LocationUtils.getDoubleSequencerValues(it) != null && LocationUtils.getDoubleSequencerValues(it)!!.isNotEmpty() })
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
            120,
            DefaultDescriptions.DURATION,
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Amount",
            15,
            "The amount of falling blocks the circle of the bloom is made of. This amount spawns every tick.",
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
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}