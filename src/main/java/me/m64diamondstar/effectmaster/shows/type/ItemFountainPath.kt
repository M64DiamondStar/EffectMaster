package me.m64diamondstar.effectmaster.shows.type

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.locations.LocationUtils
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class ItemFountainPath(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {

        try {
            val path = LocationUtils.getLocationPathFromString(getSection().getString("Path")!!)
            if(path.size < 2) return
            val material = if (getSection().get("Material") != null) Material.valueOf(
                getSection().getString("Material")!!.uppercase()
            ) else Material.STONE
            val customModelData = if(getSection().get("CustomModelData") != null) getSection().getInt("CustomModelData") else 0
            val velocity =
                if (getSection().get("Velocity") != null)
                    if (LocationUtils.getVectorFromString(getSection().getString("Velocity")!!) != null)
                        LocationUtils.getVectorFromString(getSection().getString("Velocity")!!)!!
                    else Vector(0.0, 0.0, 0.0)
                else Vector(0.0, 0.0, 0.0)
            val randomizer =
                if (getSection().get("Randomizer") != null) getSection().getDouble("Randomizer") / 10 else 0.0
            val speed = if (getSection().get("Speed") != null) getSection().getDouble("Speed") * 0.05 else 0.05
            val lifetime = if (getSection().get("Lifetime") != null) getSection().getInt("Lifetime") else 40

            val frequency = if (getSection().get("Frequency") != null) getSection().getInt("Frequency") else 5

            val smooth = if (getSection().get("Smooth") != null) getSection().getBoolean("Smooth") else true

            if(speed <= 0){
                EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
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
                                spawnItem(LocationUtils.calculateBezierPoint(path, c + 1.0 / duration / entitiesPerTick * i2), material, customModelData, lifetime, randomizer, velocity, players)
                            else
                                spawnItem(LocationUtils.calculatePolygonalChain(path, c + 1.0 / duration / entitiesPerTick * i2), material, customModelData, lifetime, randomizer, velocity, players)
                    }

                    /*
                        The amount of entities per block is bigger than the frequency
                        => No need to spawn extra entities
                    */
                    else {
                        if(smooth)
                            spawnItem(LocationUtils.calculateBezierPoint(path, c), material, customModelData, lifetime, randomizer, velocity, players)
                        else
                            spawnItem(LocationUtils.calculatePolygonalChain(path, c), material, customModelData, lifetime, randomizer, velocity, players)
                    }

                    c += 1.0 / duration
                }
            }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
        }catch (ex: Exception){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("Possible errors: ")
            EffectMaster.plugin.logger.warning("- The item you entered doesn't exist.")
            EffectMaster.plugin.logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    private fun spawnItem(location: Location, material: Material, customModelData: Int, lifetime: Int, randomizer: Double,
                          velocity: Vector, players: List<Player>?) {
// Create item
        val item = location.world!!.spawnEntity(location, EntityType.DROPPED_ITEM) as Item
        item.pickupDelay = Integer.MAX_VALUE
        item.itemStack = ItemStack(material)
        if(item.itemStack.itemMeta != null) {
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
            item.remove()
        }, lifetime.toLong())
    }

    override fun getType(): Type {
        return Type.ITEM_FOUNTAIN_PATH
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "ITEM_FOUNTAIN_PATH"))
        list.add(Pair("Path", "world, 0, 0, 0; 3, 3, 3"))
        list.add(Pair("Velocity", "0, 0, 0"))
        list.add(Pair("Material", "BLUE_STAINED_GLASS"))
        list.add(Pair("CustomModelData", 0))
        list.add(Pair("Lifetime", 40))
        list.add(Pair("Randomizer", 0))
        list.add(Pair("Speed", 1))
        list.add(Pair("Frequency", 5))
        list.add(Pair("Smooth", true))
        list.add(Pair("Delay", 0))
        return list
    }
}