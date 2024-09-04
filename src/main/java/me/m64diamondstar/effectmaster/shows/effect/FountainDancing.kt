package me.m64diamondstar.effectmaster.shows.effect

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.random.Random

class FountainDancing : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return

            // Doesn't need to play the show if it can't be viewed
            if(!location.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
                getSection(effectShow, id).getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Fountain with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()
            val sequencer = LocationUtils.getTripleSequencerValues(if(getSection(effectShow, id).getString("Sequencer") != null) getSection(effectShow, id).getString("Sequencer")!! else "0:0.0,0.75,0.0;") ?: LocationUtils.getTripleSequencerValues("0:0.0,0.75,0.0;")!!
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getInt("Duration") else {
                if (getSection(effectShow, id).get("Length") != null) getSection(effectShow, id).getInt("Length") else 20
            }
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 1
            val randomizer =
                if (getSection(effectShow, id).get("Randomizer") != null) getSection(effectShow, id).getDouble("Randomizer") / 10 else 0.0

            object : BukkitRunnable() {
                var c = 0
                override fun run() {
                    if (c == duration) {
                        this.cancel()
                        return
                    }

                    repeat(amount) {
                        val (width, height, depth) = interpolateKeyframes(sequencer, c)

                        val fallingBlock = location.world!!.spawnFallingBlock(location, blockData)
                        fallingBlock.dropItem = false
                        fallingBlock.isPersistent = false
                        fallingBlock.persistentDataContainer.set(
                            NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"),
                            PersistentDataType.BOOLEAN, true
                        )

                        fallingBlock.velocity = Vector(
                            width + (Random.nextInt(0, 1000).toDouble() / 1000) * (randomizer * 2) - randomizer,
                            height + (Random.nextInt(0, 1000).toDouble() / 1000) * (randomizer * 2) - randomizer / 3,
                            depth + (Random.nextInt(0, 1000).toDouble() / 1000) * (randomizer * 2) - randomizer
                        )

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
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        } catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    private fun interpolateKeyframes(sequencer: Map<Int, Triple<Double, Double, Double>>, tick: Int): Triple<Double, Double, Double> {
        val keys = sequencer.keys.sorted()
        val (prevKey, nextKey) = findKeyframes(keys, tick)

        val prevValue = sequencer[prevKey] ?: return Triple(0.0, 0.0, 0.0)
        val nextValue = sequencer[nextKey] ?: prevValue

        if (prevKey == nextKey) {
            return prevValue
        }

        val t = (tick - prevKey).toDouble() / (nextKey - prevKey)
        val width = prevValue.first + t * (nextValue.first - prevValue.first)
        val height = prevValue.second + t * (nextValue.second - prevValue.second)
        val depth = prevValue.third + t * (nextValue.third - prevValue.third)

        return Triple(width, height, depth)
    }

    private fun findKeyframes(keys: List<Int>, tick: Int): Pair<Int, Int> {
        var prevKey = keys.first()
        var nextKey = keys.last()

        for (key in keys) {
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

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Location", "world, 0, 0, 0", DefaultDescriptions.LOCATION, {it}){ LocationUtils.getLocationFromString(it) != null })
        list.add(Parameter("Sequencer", "0: 0.0, 0.0, 0.0; 25: 0.0, 0.75, 0.0; 50: 0.3, 0.75, 0.0; 75: 0.0, 0.75, 0.0; 100: -0.3, 0.75, 0.0; 125: 0.0, 0.75, 0.0",
                "With the sequencer, you can edit the velocity of the fountain over time. " +
                "The first number is the time in ticks, the second is the velocity width, the third is the velocity height and the fourth is the velocity depth. (ticks:width,height,depth)" +
                "You can also add multiple values separated by semicolons. (ticks1:width1,height1,depth1; ticks2:width2,height2,depth2; ...)", {it}) { LocationUtils.getTripleSequencerValues(it) != null &&LocationUtils.getTripleSequencerValues(it)!!.isNotEmpty() })
        list.add(Parameter("Block", "BLUE_STAINED_GLASS", DefaultDescriptions.BLOCK, {it.uppercase()}){ Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, {it}){ true })
        list.add(Parameter("Duration", 125, DefaultDescriptions.DURATION, {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Randomizer", 0.0, "This randomizes the value of the velocity a bit. The higher the value, the more the velocity changes. It's best keeping this between 0 and 1.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("Amount", 1, "The amount of blocks to spawn each tick.", {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }
}