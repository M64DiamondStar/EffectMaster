package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class ParticleLine(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {

        try {
            val fromLocation = LocationUtils.getLocationFromString(getSection().getString("FromLocation")!!) ?: return
            val toLocation = LocationUtils.getLocationFromString(getSection().getString("ToLocation")!!) ?: return
            val particle = getSection().getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection().get("Amount") != null) getSection().getInt("Amount") else 0
            val speed = if (getSection().get("Speed") != null) getSection().getDouble("Speed") * 0.05 else 0.05
            val dX = if (getSection().get("dX") != null) getSection().getDouble("dX") else 0.0
            val dY = if (getSection().get("dY") != null) getSection().getDouble("dY") else 0.0
            val dZ = if (getSection().get("dZ") != null) getSection().getDouble("dZ") else 0.0
            val force = if (getSection().get("Force") != null) getSection().getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0

            if(speed <= 0){
                EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
                Bukkit.getLogger().warning("The speed has to be greater than 0!")
                return
            }

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

            object : BukkitRunnable() {
                var c = 0
                var location: Location = fromLocation
                override fun run() {
                    if (c > finalMove) {
                        cancel()
                        return
                    }
                    when (particle) {
                        Particle.REDSTONE, Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT -> {
                            val color = Colors.getJavaColorFromString(getSection().getString("Color")!!) ?: java.awt.Color(0, 0, 0)
                            val dustOptions = Particle.DustOptions(
                                Color.fromRGB(color.red, color.green, color.blue),
                                if (getSection().get("Size") != null)
                                    getSection().getInt("Size").toFloat()
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
                                if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!.uppercase()) else Material.STONE
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
                                if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!.uppercase()) else Material.STONE
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
                    location.add(x, y, z)
                    c++
                }
            }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The particle you entered doesn't exist. Please choose a valid type.")
        }

    }

    override fun getType(): Type {
        return Type.PARTICLE_LINE
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "PARTICLE_LINE"))
        list.add(Pair("FromLocation", "world, 0, 0, 0"))
        list.add(Pair("ToLocation", "world, 0, 3, 0"))
        list.add(Pair("Particle", "SMOKE_NORMAL"))
        list.add(Pair("Color", "0, 0, 0"))
        list.add(Pair("Block", "STONE"))
        list.add(Pair("Amount", 1))
        list.add(Pair("Speed", 1))
        list.add(Pair("dX", 1))
        list.add(Pair("dY", 1))
        list.add(Pair("dZ", 1))
        list.add(Pair("Force", false))
        list.add(Pair("Delay", 0))
        return list
    }
}