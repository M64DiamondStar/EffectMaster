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

class ParticlePath(effectShow: EffectShow, private val id: Int) : Effect(effectShow, id) {

    override fun execute(players: List<Player>?) {

        try {
            val path = LocationUtils.getLocationPathFromString(getSection().getString("Path")!!)
            if(path.size < 2) return

            val particle = getSection().getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection().get("Amount") != null) getSection().getInt("Amount") else 0
            val speed = if (getSection().get("Speed") != null) getSection().getDouble("Speed") * 0.05 else 0.05
            val dX = if (getSection().get("dX") != null) getSection().getDouble("dX") else 0.0
            val dY = if (getSection().get("dY") != null) getSection().getDouble("dY") else 0.0
            val dZ = if (getSection().get("dZ") != null) getSection().getDouble("dZ") else 0.0
            val force = if (getSection().get("Force") != null) getSection().getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0

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
                                spawnParticle(LocationUtils.calculateBezierPoint(path, c + 1.0 / duration / entitiesPerTick * i2),
                                    particle, amount, dX, dY, dZ, extra, force, players)
                            else
                                spawnParticle(LocationUtils.calculatePolygonalChain(path, c + 1.0 / duration / entitiesPerTick * i2),
                                    particle, amount, dX, dY, dZ, extra, force, players)
                    }

                    /*
                        The amount of entities per block is bigger than the frequency
                        => No need to spawn extra entities
                    */
                    else {
                        if(smooth)
                            spawnParticle(LocationUtils.calculateBezierPoint(path, c), particle, amount, dX, dY, dZ, extra, force, players)
                        else
                            spawnParticle(LocationUtils.calculatePolygonalChain(path, c), particle, amount, dX, dY, dZ, extra, force, players)
                    }

                    c += 1.0 / duration
                }
            }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("Possible errors: ")
            EffectMaster.plugin.logger.warning("- The particle you entered doesn't exist.")
            EffectMaster.plugin.logger.warning("- The location/world doesn't exist or is unloaded")
        }

    }

    private fun spawnParticle(location: Location, particle: Particle, amount: Int, dX: Double, dY: Double, dZ: Double,
                              extra: Double, force: Boolean, players: List<Player>?) {
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
    }

    override fun getType(): Type {
        return Type.PARTICLE_PATH
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "PARTICLE_PATH"))
        list.add(Pair("Path", "world, 0, 0, 0; 3, 3, 3"))
        list.add(Pair("Particle", "SMOKE_NORMAL"))
        list.add(Pair("Color", "0, 0, 0"))
        list.add(Pair("Block", "STONE"))
        list.add(Pair("Amount", 1))
        list.add(Pair("Speed", 1))
        list.add(Pair("Frequency", 5))
        list.add(Pair("Smooth", true))
        list.add(Pair("dX", 1))
        list.add(Pair("dY", 1))
        list.add(Pair("dZ", 1))
        list.add(Pair("Force", false))
        list.add(Pair("Delay", 0))
        return list
    }
}