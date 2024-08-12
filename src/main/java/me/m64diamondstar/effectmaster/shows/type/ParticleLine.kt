package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.absoluteValue
import kotlin.math.max

class ParticleLine() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("ToLocation")!!) ?: return
            val particle = getSection(effectShow, id).getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 0
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05
            val dX = if (getSection(effectShow, id).get("dX") != null) getSection(effectShow, id).getDouble("dX") else 0.0
            val dY = if (getSection(effectShow, id).get("dY") != null) getSection(effectShow, id).getDouble("dY") else 0.0
            val dZ = if (getSection(effectShow, id).get("dZ") != null) getSection(effectShow, id).getDouble("dZ") else 0.0
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0

            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
                return
            }

            val distance = fromLocation.distance(toLocation)

            val deX: Double = (toLocation.x - fromLocation.x) / speed
            val deY: Double = (toLocation.y - fromLocation.y) / speed
            val deZ: Double = (toLocation.z - fromLocation.z) / speed

            // How long the effect is expected to last.
            val duration = max(max(deX.absoluteValue, deY.absoluteValue), deZ.absoluteValue)

            val x: Double = deX / duration / 20.0 * (speed * 20.0)
            val y: Double = deY / duration / 20.0 * (speed * 20.0)
            val z: Double = deZ / duration / 20.0 * (speed * 20.0)

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

                        repeat(entitiesPerTick.toInt()){
                            spawnParticle(adjustedLocation, particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                            adjustedLocation.add(adjustedX, adjustedY, adjustedZ)
                        }
                    }

                    /* The amount of entities per block is bigger than the frequency
                        => No need to spawn extra entities
                     */
                    else {
                        spawnParticle(location, particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                    }

                    location.add(x, y, z)
                    c++
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Line with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The particle you entered doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }

    }

    private fun spawnParticle(location: Location, particle: Particle, amount: Int, dX: Double, dY: Double, dZ: Double,
                              extra: Double, force: Boolean, players: List<Player>?, effectShow: EffectShow, id: Int) {
        when (particle) {
            Particle.REDSTONE, Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT -> {
                val color = Colors.getJavaColorFromString(getSection(effectShow, id).getString("Color")!!) ?: java.awt.Color(0, 0, 0)
                val dustOptions = Particle.DustOptions(
                    Color.fromRGB(color.red, color.green, color.blue),
                    if (getSection(effectShow, id).get("Size") != null)
                        getSection(effectShow, id).getInt("Size").toFloat()
                    else
                        1F
                )
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions)
                    }
                }
            }

            Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                val material =
                    if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, material.createBlockData(), force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, material.createBlockData())
                    }
                }
            }

            Particle.ITEM_CRACK -> {
                val material =
                    if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, ItemStack(material), force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, ItemStack(material))
                    }
                }
            }

            else -> {
                if(players == null) {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null, force)
                }else{
                    players.forEach {
                        it.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null)
                    }
                }
            }
        }
    }

    override fun getIdentifier(): String {
        return "PARTICLE_LINE"
    }

    override fun getDisplayMaterial(): Material {
        return Material.REPEATER
    }

    override fun getDescription(): String {
        return "Spawns a line of particles."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "PARTICLE_LINE"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("FromLocation", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("ToLocation", "world, 0, 3, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Particle", "SMOKE_NORMAL"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Color", "0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Block", "STONE"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Amount", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Speed", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Frequency", 5))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("dX", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("dY", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("dZ", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Force", false))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}