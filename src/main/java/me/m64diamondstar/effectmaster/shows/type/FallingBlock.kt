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
import org.bukkit.util.Vector

class FallingBlock(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {

        try {
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
            val material = if (getSection().get("Block") != null) Material.valueOf(
                getSection().getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
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

            val fallingBlock = location.world!!.spawnFallingBlock(location, blockData)
            fallingBlock.velocity = velocity
            fallingBlock.dropItem = false

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
        } catch (ex: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Falling Block with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }
    }

    override fun getType(): Type {
        return Type.FALLING_BLOCK
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "FALLING_BLOCK"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Velocity", "0, 0, 0"))
        list.add(Pair("Block", "STONE"))
        list.add(Pair("BlockData", "[]"))
        list.add(Pair("Delay", 0))
        return list
    }
}