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
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random

class FountainLine() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {

        try {
            val fromLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("FromLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("ToLocation")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return

            // Doesn't need to play the show if it can't be viewed
            if(!fromLocation.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
                getSection(effectShow, id).getString("Block")!!.uppercase()
            ) else Material.STONE

            if(!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(getSection(effectShow, id).get("BlockData") != null)
                Bukkit.createBlockData(material, getSection(effectShow, id).getString("BlockData")!!) else material.createBlockData()
            val velocity =
                if (getSection(effectShow, id).get("Velocity") != null)
                    if (LocationUtils.getVectorFromString(getSection(effectShow, id).getString("Velocity")!!) != null)
                        LocationUtils.getVectorFromString(getSection(effectShow, id).getString("Velocity")!!)!!
                    else Vector(0.0, 0.0, 0.0)
                else Vector(0.0, 0.0, 0.0)
            val randomizer =
                if (getSection(effectShow, id).get("Randomizer") != null) getSection(effectShow, id).getDouble("Randomizer") / 10 else 0.0
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 1
            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

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

            val x: Double = dX / duration / 20.0 * (speed * 20.0)
            val y: Double = dY / duration / 20.0 * (speed * 20.0)
            val z: Double = dZ / duration / 20.0 * (speed * 20.0)

            var c = 0
            val location: Location = fromLocation
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
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

                        val adjustedLocation = location.clone()
                        val adjustedX = x / entitiesPerTick
                        val adjustedY = y / entitiesPerTick
                        val adjustedZ = z / entitiesPerTick

                        repeat(entitiesPerTick.toInt()) {
                            spawnFallingBlock(adjustedLocation, blockData, randomizer, velocity, players)
                            adjustedLocation.add(adjustedX, adjustedY, adjustedZ)
                        }
                    }

                    /* The amount of entities per block is bigger than the frequency
                    => No need to spawn extra entities
                 */
                    else {
                        spawnFallingBlock(location, blockData, randomizer, velocity, players)
                    }
                }

                c++
                location.add(x, y, z)
            }, 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Line with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    private fun spawnFallingBlock(location: Location, blockData: BlockData, randomizer: Double, velocity: Vector, players: List<Player>?) {
        val fallingBlock = location.world!!.spawnFallingBlock(location, blockData)
        fallingBlock.dropItem = false
        fallingBlock.isPersistent = false
        fallingBlock.persistentDataContainer.set(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"),
            PersistentDataType.BOOLEAN, true)

        if (randomizer != 0.0)
            fallingBlock.velocity = Vector(
                velocity.x + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer,
                velocity.y + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3,
                velocity.z + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer
            )
        else
            fallingBlock.velocity = velocity

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