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
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.random.Random

class Fountain() : Effect() {

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
                EffectMaster.plugin().logger.warning("Couldn't play Fountain with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
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
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getInt("Duration") else {
                if (getSection(effectShow, id).get("Length") != null) getSection(effectShow, id).getInt("Length") else 20
            }
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 1
            val randomizer =
                if (getSection(effectShow, id).get("Randomizer") != null) getSection(effectShow, id).getDouble("Randomizer") / 10 else 0.0

            var c = 0
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->

                if (c == duration) {
                    task.cancel()
                    return@runTimer
                }

                repeat(amount) {
                    val fallingBlock = location.world!!.spawnFallingBlock(location, blockData)
                    fallingBlock.dropItem = false
                    fallingBlock.isPersistent = false
                    fallingBlock.persistentDataContainer.set(
                        NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"),
                        PersistentDataType.BOOLEAN, true
                    )

                    if (randomizer != 0.0)
                        fallingBlock.velocity = Vector(
                            velocity.x + (Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer),
                            velocity.y + (Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3),
                            velocity.z + (Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer)
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

                c++

            }, 0L, 1L)
        } catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The Block entered doesn't exist or the BlockData doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    override fun getIdentifier(): String {
        return "FOUNTAIN"
    }

    override fun getDisplayMaterial(): Material {
        return Material.WATER_BUCKET
    }

    override fun getDescription(): String {
        return "Spawn a fountain of falling blocks."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Location", "world, 0, 0, 0", DefaultDescriptions.LOCATION, {it}){ LocationUtils.getLocationFromString(it) != null })
        list.add(Parameter("Velocity", "0, 0, 0", DefaultDescriptions.VELOCITY, {it}){ LocationUtils.getVectorFromString(it) != null })
        list.add(Parameter("Block", "BLUE_STAINED_GLASS", DefaultDescriptions.BLOCK, {it.uppercase()}){ Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        list.add(Parameter("BlockData", "[]", DefaultDescriptions.BLOCK_DATA, {it}){ true })
        list.add(Parameter("Duration", 20, DefaultDescriptions.DURATION, {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Randomizer", 0.0, "This randomizes the value of the velocity a bit. The higher the value, the more the velocity changes. It's best keeping this between 0 and 1.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("Amount", 1, "The amount of blocks to spawn each tick.", {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }
}