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
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class ItemFountain(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {
        try {
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
            val material = if (getSection().get("Material") != null) Material.valueOf(
                getSection().getString("Material")!!.uppercase()
            ) else Material.STONE
            val customModelData =
                if (getSection().get("CustomModelData") != null) getSection().getInt("CustomModelData") else 0
            val velocity =
                if (getSection().get("Velocity") != null)
                    if (LocationUtils.getVectorFromString(getSection().getString("Velocity")!!) != null)
                        LocationUtils.getVectorFromString(getSection().getString("Velocity")!!)!!
                    else Vector(0.0, 0.0, 0.0)
                else Vector(0.0, 0.0, 0.0)
            val length = if (getSection().get("Length") != null) getSection().getInt("Length") else 1
            val randomizer =
                if (getSection().get("Randomizer") != null) getSection().getDouble("Randomizer") / 10 else 0.0
            val lifetime = if (getSection().get("Lifetime") != null) getSection().getInt("Lifetime") else 40

            object : BukkitRunnable() {
                var c = 0
                override fun run() {
                    if (c == length) {
                        this.cancel()
                        return
                    }

                    // Create item
                    val item = location.world!!.spawnEntity(location, EntityType.DROPPED_ITEM) as Item
                    item.pickupDelay = Integer.MAX_VALUE
                    item.isPersistent = false
                    item.itemStack = ItemStack(material)
                    if (item.itemStack.itemMeta != null) {
                        val meta = item.itemStack.itemMeta!!
                        meta.setCustomModelData(customModelData)
                        item.itemStack.itemMeta = meta
                    }

                    // Fix velocity
                    if (randomizer != 0.0)
                        item.velocity = Vector(
                            velocity.x + Math.random() * (randomizer * 2) - randomizer,
                            velocity.y + Math.random() * (randomizer * 2) - randomizer / 3,
                            velocity.z + Math.random() * (randomizer * 2) - randomizer
                        )
                    else
                        item.velocity = velocity

                    // Register dropped item (this prevents it from merging with others)
                    ShowUtils.addDroppedItem(item)

                    // Make private effect if needed
                    if (players != null && EffectMaster.isProtocolLibLoaded)
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (!players.contains(player)) {
                                val protocolManager = ProtocolLibrary.getProtocolManager()
                                val removePacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
                                removePacket.intLists.write(0, listOf(item.entityId))
                                protocolManager.sendServerPacket(player, removePacket)
                            }
                        }

                    // Remove item after given time
                    Bukkit.getScheduler().scheduleSyncDelayedTask(EffectMaster.plugin, {
                        if(item.isValid)
                            item.remove()
                    }, lifetime.toLong())

                    c++
                }
            }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
        }catch (ex: Exception){
            EffectMaster.plugin.logger.warning("Couldn't play Item Fountain with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("Possible errors: ")
            EffectMaster.plugin.logger.warning("- The item you entered doesn't exist.")
            EffectMaster.plugin.logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    override fun getType(): Type {
        return Type.ITEM_FOUNTAIN
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "ITEM_FOUNTAIN"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Velocity", "0, 0, 0"))
        list.add(Pair("Material", "BLUE_STAINED_GLASS"))
        list.add(Pair("CustomModelData", 0))
        list.add(Pair("Length", 20))
        list.add(Pair("Lifetime", 40))
        list.add(Pair("Randomizer", 0))
        list.add(Pair("Delay", 0))
        return list
    }
}