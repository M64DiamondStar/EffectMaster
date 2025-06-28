package me.m64diamondstar.effectmaster.shows.effect

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
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random

class ItemFountainLine() : Effect() {

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

            val material = if (getSection(effectShow, id).get("Material") != null) Material.valueOf(
                getSection(effectShow, id).getString("Material")!!.uppercase()
            ) else Material.STONE
            val customModelData = if(getSection(effectShow, id).get("CustomModelData") != null) getSection(effectShow, id).getInt("CustomModelData") else 0
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
            val lifetime = if (getSection(effectShow, id).get("Lifetime") != null) getSection(effectShow, id).getInt("Lifetime") else 40

            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play Item Fountain Line with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
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
                                spawnItem(
                                    adjustedLocation,
                                    material,
                                    customModelData,
                                    lifetime,
                                    randomizer,
                                    velocity,
                                    players
                                )
                                adjustedLocation.add(adjustedX, adjustedY, adjustedZ)
                            }
                        }

                        /* The amount of entities per block is bigger than the frequency
                        => No need to spawn extra entities
                     */
                        else {
                            spawnItem(location, material, customModelData, lifetime, randomizer, velocity, players)
                        }
                    }
                    location.add(x, y, z)
                    c++
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The item you entered doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    private fun spawnItem(location: Location, material: Material, customModelData: Int, lifetime: Int, randomizer: Double,
                          velocity: Vector, players: List<Player>?) {
// Create item
        val item = location.world!!.spawnEntity(location, EntityType.ITEM) as Item
        item.pickupDelay = Integer.MAX_VALUE
        item.isPersistent = false
        item.persistentDataContainer.set(NamespacedKey(EffectMaster.plugin(), "effectmaster-entity"),
            PersistentDataType.BOOLEAN, true)
        item.itemStack = ItemStack(material)
        if(item.itemStack.itemMeta != null) {
            val meta = item.itemStack.itemMeta!!
            meta.setCustomModelData(customModelData)
            item.itemStack.itemMeta = meta
        }

        // Fix velocity
        if (randomizer != 0.0)
            item.velocity = Vector(
                velocity.x + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer,
                velocity.y + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer / 3,
                velocity.z + Random.nextInt(0, 1000).toDouble() / 1000 * randomizer * 2 - randomizer
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
        EffectMaster.getFoliaLib().scheduler.runLater({ task ->
            if (item.isValid) {
                item.remove()
                ShowUtils.removeDroppedItem(item)
            }
        }, lifetime.toLong())
    }

    override fun getIdentifier(): String {
        return "ITEM_FOUNTAIN_LINE"
    }

    override fun getDisplayMaterial(): Material {
        return Material.LINGERING_POTION
    }

    override fun getDescription(): String {
        return "Spawns a fountain line of dropped items with customizable velocity."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
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
        list.add(Parameter(
            "Material",
            "BLUE_STAINED_GLASS",
            DefaultDescriptions.BLOCK,
            {it.uppercase()},
            { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        )
        list.add(Parameter(
            "CustomModelData",
            0,
            DefaultDescriptions.BLOCK_DATA,
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Lifetime",
            40,
            "How long the item should stay before they get removed. Items don't automatically get removed when they hit the ground.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
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