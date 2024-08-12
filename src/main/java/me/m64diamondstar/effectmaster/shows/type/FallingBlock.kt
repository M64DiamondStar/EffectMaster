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

class FallingBlock() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val material = if (getSection(effectShow, id).get("Block") != null) Material.valueOf(
                getSection(effectShow, id).getString("Block")!!.uppercase()
            ) else Material.STONE

            if (!material.isBlock) {
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

            val fallingBlock = location.world!!.spawnFallingBlock(location, blockData)
            fallingBlock.velocity = velocity
            fallingBlock.dropItem = false
            fallingBlock.isPersistent = false

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
        } catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Falling Block with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The Block entered doesn't exist or the BlockData doesn't exist.")
        }
    }

    override fun getIdentifier(): String {
        return "FALLING_BLOCK"
    }

    override fun getDisplayMaterial(): Material {
        return Material.SAND
    }

    override fun getDescription(): String {
        return "Spawns a falling block with customizable velocity."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "FALLING_BLOCK"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Location", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Velocity", "0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Block", "STONE"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("BlockData", "[]"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}