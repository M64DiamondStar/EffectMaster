package me.m64diamondstar.effectmaster.shows.type

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import org.bukkit.*
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class FountainPath() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val path = LocationUtils.getLocationPathFromString(getSection(effectShow, id).getString("Path")!!)
            if(path.size < 2) return

            // Doesn't need to play the show if it can't be viewed
            if(!path[0].chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
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

            val smooth = if (getSection(effectShow, id).get("Smooth") != null) getSection(effectShow, id).getBoolean("Smooth") else true

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play Fountain Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
                return
            }

            var distance = 0.0
            for(loc in 1 until path.size){
                distance += path[loc - 1].distance(path[loc])
            }

            // How long the effect is expected to last.
            val duration = distance / speed

            object : BukkitRunnable() {
                var c = 0.0
                override fun run() {
                    if (c >= 1) {
                        cancel()
                        return
                    }

                    /*
                    duration / distance = how many entities per block?
                    if this is smaller than the frequency it has to spawn more entities in one tick

                    The frequency / entities per block = how many entities per tick
                    */
                    if (duration / distance < frequency) {
                        val entitiesPerTick = frequency / (duration / distance)
                        for (i2 in 1..entitiesPerTick.toInt())
                            if(smooth)
                                spawnFallingBlock(LocationUtils.calculateBezierPoint(path, c + 1.0 / duration / entitiesPerTick * i2), blockData, randomizer, velocity, players)
                            else
                                spawnFallingBlock(LocationUtils.calculatePolygonalChain(path, c + 1.0 / duration / entitiesPerTick * i2), blockData, randomizer, velocity, players)
                    }

                    /*
                        The amount of entities per block is bigger than the frequency
                        => No need to spawn extra entities
                    */
                    else {
                        if(smooth)
                            spawnFallingBlock(LocationUtils.calculateBezierPoint(path, c), blockData, randomizer, velocity, players)
                        else
                            spawnFallingBlock(LocationUtils.calculatePolygonalChain(path, c), blockData, randomizer, velocity, players)
                    }

                    c += 1.0 / duration
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Fountain Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
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

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Path", "world, 0, 0, 0", "The path the origin of the fountain follows using the format of " +
                "\"world, x1, y1, z1; x2, y2, z2; x3, y3, z3\". You can of course repeat this process as much as you would like. Use a ; to separate different locations.", {it}){ LocationUtils.getLocationPathFromString(it).isNotEmpty() })
        list.add(Parameter("Velocity", "0, 0, 0", DefaultDescriptions.VELOCITY, {it}){ LocationUtils.getVectorFromString(it) != null })
        list.add(Parameter("Block", "BLUE_STAINED_GLASS", DefaultDescriptions.BLOCK, {it.uppercase()}){ Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, {it}){ true })
        list.add(Parameter("Duration", 1, DefaultDescriptions.DURATION, {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Randomizer", 0.0, "This randomizes the value of the velocity a bit. The higher the value, the more the velocity changes. It's best keeping this between 0 and 1.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("Speed", 1, "The speed of the fountain path progression. Measured in blocks/second.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0 })
        list.add(Parameter("Frequency", 5, "In Minecraft a new entity or particle spawns every tick, but when the speed is very high an empty space comes between two entities or particles. To fix that you can use the frequency parameter. The frequency is how many entities/particles there should be every block. This effect only activates when the speed is too big that the amount of entities or particles per block is lower than the frequency.", {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Smooth", true, "If true, the blocks will be spawned with a bezier curve. If false, the blocks will be spawned with a polygonal chain.", {it.toBoolean()}) { it.toBoolean() })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }
}