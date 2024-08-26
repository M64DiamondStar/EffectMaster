package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class ParticlePath() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {

        try {
            val path = LocationUtils.getLocationPathFromString(getSection(effectShow, id).getString("Path")!!)
            if(path.size < 2) return

            val particle = getSection(effectShow, id).getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 0
            val speed = if (getSection(effectShow, id).get("Speed") != null) getSection(effectShow, id).getDouble("Speed") * 0.05 else 0.05
            val dX = if (getSection(effectShow, id).get("dX") != null) getSection(effectShow, id).getDouble("dX") else 0.0
            val dY = if (getSection(effectShow, id).get("dY") != null) getSection(effectShow, id).getDouble("dY") else 0.0
            val dZ = if (getSection(effectShow, id).get("dZ") != null) getSection(effectShow, id).getDouble("dZ") else 0.0
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0

            val frequency = if (getSection(effectShow, id).get("Frequency") != null) getSection(effectShow, id).getInt("Frequency") else 5

            val smooth = if (getSection(effectShow, id).get("Smooth") != null) getSection(effectShow, id).getBoolean("Smooth") else true

            if(speed <= 0){
                EffectMaster.plugin().logger.warning("Couldn't play effect with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
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
                                    particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                            else
                                spawnParticle(LocationUtils.calculatePolygonalChain(path, c + 1.0 / duration / entitiesPerTick * i2),
                                    particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                    }

                    /*
                        The amount of entities per block is bigger than the frequency
                        => No need to spawn extra entities
                    */
                    else {
                        if(smooth)
                            spawnParticle(LocationUtils.calculateBezierPoint(path, c), particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                        else
                            spawnParticle(LocationUtils.calculatePolygonalChain(path, c), particle, amount, dX, dY, dZ, extra, force, players, effectShow, id)
                    }

                    c += 1.0 / duration
                }
            }.runTaskTimer(EffectMaster.plugin(), 0L, 1L)
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Path with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
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
        return "PARTICLE_PATH"
    }

    override fun getDisplayMaterial(): Material {
        return Material.COMPARATOR
    }

    override fun getDescription(): String {
        return "Spawns a path of particles."
    }

    override fun isSync(): Boolean {
        return true
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Particle", "CLOUD", DefaultDescriptions.PARTICLE, {it.uppercase()}) { it in Particle.entries.map { it.name } })
        list.add(Parameter("Path", "world, 0, 0, 0; 1, 1, 1", "The path the particles follows using the format of " +
                "\"world, x1, y1, z1; x2, y2, z2; x3, y3, z3\". You can of course repeat this process as much as you would like. Use a ; to separate different locations.", {it}) { LocationUtils.getLocationPathFromString(it).isNotEmpty() })
        list.add(Parameter("Amount", 50, "The amount of particles to spawn.", {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("Speed", 1, "The speed of the particle line. Measured in blocks/second.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("Frequency", 5, "In Minecraft a new entity or particle spawns every tick, but when the speed is very high an empty space comes between two entities or particles. To fix that you can use the frequency parameter. The frequency is how many entities/particles there should be every block. This effect only activates when the speed is too big that the amount of entities or particles per block is lower than the frequency.", {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("dX", 0.3, "The delta X, the value of this decides how much the area where the particle spawns will extend over the x-axis.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("dY", 0.3, "The delta Y, the value of this decides how much the area where the particle spawns will extend over the y-axis.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("dZ", 0.3, "The delta Z, the value of this decides how much the area where the particle spawns will extend over the z-axis.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("Force", false, "Whether the particle should be forcibly rendered by the player or not.", {it.toBoolean()}) { it.toBooleanStrictOrNull() != null })
        list.add(Parameter("Smooth", true, "If true, the particles will be spawned with a bezier curve. If false, the particles will be spawned with a polygonal chain.", {it.toBoolean()}) { it.toBooleanStrictOrNull() != null })
        list.add(Parameter("Size", 0.5f, "The size of the particle, only works for REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT.", {it.toFloat()}) { it.toFloatOrNull() != null && it.toFloat() >= 0.0 })
        list.add(Parameter("Color", "0, 0, 0", "The color of the particle, only works for REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT. Formatted in RGB.", {it}) { Colors.getJavaColorFromString(it) != null })
        list.add(Parameter("Block", "STONE", "The block id of the particle, only works for BLOCK_CRACK, BLOCK_DUST, FALLING_DUST and ITEM_CRACK.", {it}) { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }
}