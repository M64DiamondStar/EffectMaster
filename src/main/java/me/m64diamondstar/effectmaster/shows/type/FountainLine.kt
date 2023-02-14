package me.m64diamondstar.effectmaster.shows.type

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class FountainLine(effectShow: EffectShow, id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {
        val fromLocation = LocationUtils.getLocationFromString(getSection().getString("FromLocation")!!) ?: return
        val toLocation = LocationUtils.getLocationFromString(getSection().getString("ToLocation")!!) ?: return
        val material = if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!.uppercase()) else Material.STONE
        val velocity =
            if (getSection().get("Velocity") != null)
                if(LocationUtils.getVectorFromString(getSection().getString("Velocity")!!) != null)
                    LocationUtils.getVectorFromString(getSection().getString("Velocity")!!)!!
                else Vector(0.0, 0.0, 0.0)
            else Vector(0.0, 0.0, 0.0)
        val randomizer = if (getSection().get("Randomizer") != null) getSection().getDouble("Randomizer") / 10 else 0.0
        val speed = if (getSection().get("Speed") != null) getSection().getInt("Speed") * 0.05 else 0.05

        val moveX: Double = (toLocation.x - fromLocation.x) / speed
        val moveY: Double = (toLocation.y - fromLocation.y) / speed
        val moveZ: Double = (toLocation.z - fromLocation.z) / speed

        var nx = moveX
        var ny = moveY
        var nz = moveZ
        if (nx < 0) nx = -nx
        if (ny < 0) ny = -ny
        if (nz < 0) nz = -nz

        var move = nx
        if (ny > nx && ny > nz) move = ny
        if (nz > ny && nz > nx) move = nz

        val x: Double = moveX / move / 20.0 * (speed * 20.0)
        val y: Double = moveY / move / 20.0 * (speed * 20.0)
        val z: Double = moveZ / move / 20.0 * (speed * 20.0)

        val finalMove = move

        object: BukkitRunnable(){
            var c = 0
            var location: Location = fromLocation
            override fun run() {
                if (c > finalMove) {
                    cancel()
                    return
                }

                val fallingBlock = location.world!!.spawnFallingBlock(location, material.createBlockData())
                fallingBlock.dropItem = false

                if(randomizer != 0.0)
                    fallingBlock.velocity = Vector(velocity.x + Math.random() * (randomizer * 2) - randomizer,
                        velocity.y + Math.random() * (randomizer * 2) - randomizer / 3,
                        velocity.z + Math.random() * (randomizer * 2) - randomizer)
                else
                    fallingBlock.velocity = velocity

                ShowUtils.addFallingBlock(fallingBlock)

                if(players != null && EffectMaster.isProtocolLibLoaded)
                    for(player in Bukkit.getOnlinePlayers()){
                        if(!players.contains(player)){
                            val protocolManager = ProtocolLibrary.getProtocolManager()
                            val removePacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
                            removePacket.intLists.write(0, listOf(fallingBlock.entityId))
                            protocolManager.sendServerPacket(player, removePacket)
                        }
                    }
                location.add(x, y, z)
                c++
            }
        }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
    }

    override fun getType(): Type {
        return Type.FOUNTAIN_LINE
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
        list.add(Pair("Randomizer", 0))
        list.add(Pair("Speed", 0.05))
        list.add(Pair("Delay", 0))
        return list
    }
}