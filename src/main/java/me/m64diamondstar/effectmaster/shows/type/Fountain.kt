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
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class Fountain(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {

        try {
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return

            // Doesn't need to play the show if it can't be viewed
            if(!location.chunk.isLoaded || Bukkit.getOnlinePlayers().isEmpty())
                return

            val material = if (getSection().get("Block") != null) Material.valueOf(
                getSection().getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play Fountain with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
                EffectMaster.plugin().logger.warning("The material entered is not a block.")
                return
            }

            val blockData = if(getSection().get("BlockData") != null)
                Bukkit.createBlockData(material, getSection().getString("BlockData")!!) else material.createBlockData()
            val velocity =
                if (getSection().get("Velocity") != null)
                    if (LocationUtils.getVectorFromString(getSection().getString("Velocity")!!) != null)
                        LocationUtils.getVectorFromString(getSection().getString("Velocity")!!)!!
                    else Vector(0.0, 0.0, 0.0)
                else Vector(0.0, 0.0, 0.0)
            val length = if (getSection().get("Length") != null) getSection().getInt("Length") else 1
            val randomizer =
                if (getSection().get("Randomizer") != null) getSection().getDouble("Randomizer") / 10 else 0.0

            object : BukkitRunnable() {
                var c = 0
                override fun run() {
                    if (c == length) {
                        this.cancel()
                        return
                    }

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

                    c++
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        } catch (ex: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    override fun getType(): Type {
        return Type.FOUNTAIN
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "FOUNTAIN"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Velocity", "0, 0, 0"))
        list.add(Pair("Block", "BLUE_STAINED_GLASS"))
        list.add(Pair("BlockData", "[]"))
        list.add(Pair("Length", 20))
        list.add(Pair("Randomizer", 0))
        list.add(Pair("Delay", 0))
        return list
    }
}