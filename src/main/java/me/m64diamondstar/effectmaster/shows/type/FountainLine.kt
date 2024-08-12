package me.m64diamondstar.effectmaster.shows.type

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.locations.LocationUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.max

class FountainLine() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return

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

            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
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

            object : BukkitRunnable() {
                var c = 0
                var location: Location = fromLocation
                override fun run() {
                    if (c >= duration) {
                        cancel()
                        return
                    }

                    /* duration / distance = how many entities per block?
                    if this is smaller than the frequency it has to spawn more entities in one tick

                    The frequency / entities per block = how many entities per tick*/
                    if(duration / distance < frequency) {
                        val entitiesPerTick = frequency / (duration / distance)

                        val adjustedLocation = location.clone()
                        val adjustedX = x / entitiesPerTick
                        val adjustedY = y / entitiesPerTick
                        val adjustedZ = z / entitiesPerTick

                        for(i in 1..entitiesPerTick.toInt()){
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

                    c++
                    location.add(x, y, z)
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (ex: Exception){
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

        if (randomizer != 0.0)
            fallingBlock.velocity = Vector(
                velocity.x + Math.random() * (randomizer * 2) - randomizer,
                velocity.y + Math.random() * (randomizer * 2) - randomizer / 3,
                velocity.z + Math.random() * (randomizer * 2) - randomizer
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

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "FOUNTAIN_LINE"))
        list.add(Pair("FromLocation", "world, 0, 0, 0"))
        list.add(Pair("ToLocation", "world, 0, 3, 0"))
        list.add(Pair("Velocity", "0, 0, 0"))
        list.add(Pair("Block", "BLUE_STAINED_GLASS"))
        list.add(Pair("BlockData", "[]"))
        list.add(Pair("Randomizer", 0))
        list.add(Pair("Speed", 1))
        list.add(Pair("Frequency", 5))
        list.add(Pair("Delay", 0))
        return list
    }
}