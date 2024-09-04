package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.roundToInt

class ParticleEmitter() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        try{
            val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val particle = getSection(effectShow, id).getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 0
            val dX = if (getSection(effectShow, id).get("dX") != null) getSection(effectShow, id).getDouble("dX") else 0.0
            val dY = if (getSection(effectShow, id).get("dY") != null) getSection(effectShow, id).getDouble("dY") else 0.0
            val dZ = if (getSection(effectShow, id).get("dZ") != null) getSection(effectShow, id).getDouble("dZ") else 0.0
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getInt("Duration") else {
                if (getSection(effectShow, id).get("Length") != null) getSection(effectShow, id).getInt("Length") else 20
            }
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val startUp = if (getSection(effectShow, id).get("StartUp") != null) getSection(effectShow, id).getDouble("StartUp") else 0.0
            val extra = if(amount == 0) 1.0 else 0.0

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

                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {
                            if(c == duration){
                                this.cancel()
                                return
                            }
                            if(players == null) {
                                location.world!!.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, dustOptions, force
                                )
                            }else{
                                players.forEach {
                                    it.spawnParticle(particle, location,
                                        if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                        dX, dY, dZ, extra, dustOptions)
                                }
                            }
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin(), 0L, 1L)
                }

                Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                    val material =
                        if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {

                            if(c == duration){
                                this.cancel()
                                return
                            }
                            if(players == null) {
                                location.world!!.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, material.createBlockData(), force
                                )
                            }else{
                                players.forEach {
                                    it.spawnParticle(
                                        particle, location,
                                        if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                        dX, dY, dZ, extra, material.createBlockData())
                                }
                            }
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin(), 0L, 1L)
                }

                Particle.ITEM_CRACK -> {
                    val material =
                        if (getSection(effectShow, id).get("Block") != null) Material.valueOf(getSection(effectShow, id).getString("Block")!!.uppercase()) else Material.STONE
                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {

                            if(c == duration){
                                this.cancel()
                                return
                            }
                            if(players == null) {
                                location.world!!.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, ItemStack(material), force
                                )
                            }else{
                                players.forEach {
                                    it.spawnParticle(
                                        particle, location,
                                        if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                        dX, dY, dZ, extra, ItemStack(material))
                                }
                            }
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin(), 0L, 1L)
                }

                else -> {
                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {
                            if(c == duration){
                                this.cancel()
                                return
                            }
                            if(players == null) {
                                location.world!!.spawnParticle(
                                    particle, location,
                                    if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                    dX, dY, dZ, extra, null, force
                                )
                            }else{
                                players.forEach {
                                    it.spawnParticle(particle, location,
                                        if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount,
                                        dX, dY, dZ, extra, null)
                                }
                            }
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin(), 0L, 1L)
                }
            }
        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Emitter with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The particle you entered doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    override fun getIdentifier(): String {
        return "PARTICLE_EMITTER"
    }

    override fun getDisplayMaterial(): Material {
        return Material.DISPENSER
    }

    override fun getDescription(): String {
        return "Emits particles over a specific time span."
    }

    override fun isSync(): Boolean {
        return false
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter("Particle", "CLOUD", DefaultDescriptions.PARTICLE, {it.uppercase()}) { it in Particle.entries.map { it.name } })
        list.add(Parameter("Location", "world, 0, 0, 0", DefaultDescriptions.LOCATION, {it}) { LocationUtils.getLocationFromString(it) != null })
        list.add(Parameter("Amount", 50, "The amount of particles to spawn.", {it.toInt()}) { it.toIntOrNull() != null && it.toInt() >= 0 })
        list.add(Parameter("dX", 0.3, "The delta X, the value of this decides how much the area where the particle spawns will extend over the x-axis.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("dY", 0.3, "The delta Y, the value of this decides how much the area where the particle spawns will extend over the y-axis.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("dZ", 0.3, "The delta Z, the value of this decides how much the area where the particle spawns will extend over the z-axis.", {it.toDouble()}) { it.toDoubleOrNull() != null && it.toDouble() >= 0.0 })
        list.add(Parameter("Force", false, "Whether the particle should be forcibly rendered by the player or not.", {it.toBoolean()}) { it.toBooleanStrictOrNull() != null })
        list.add(Parameter("Duration", 20, DefaultDescriptions.DURATION, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        list.add(Parameter("StartUp", 0, "The time it takes to display the full amount of particles. Set to 0 to disable it.", {it.toInt()}) { it.toFloatOrNull() != null && it.toFloat() >= 0.0 })
        list.add(Parameter("Size", 0.5f, "The size of the particle, only works for REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT.", {it.toFloat()}) { it.toFloatOrNull() != null && it.toFloat() >= 0.0 })
        list.add(Parameter("Color", "0, 0, 0", "The color of the particle, only works for REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT. Formatted in RGB.", {it}) { Colors.getJavaColorFromString(it) != null })
        list.add(Parameter("Block", "STONE", "The block id of the particle, only works for BLOCK_CRACK, BLOCK_DUST, FALLING_DUST and ITEM_CRACK.", {it.uppercase()}) { Material.entries.any { mat -> it.equals(mat.name, ignoreCase = true) } })
        list.add(Parameter("Delay", 0, DefaultDescriptions.DELAY, {it.toInt()}) { it.toLongOrNull() != null && it.toLong() >= 0 })
        return list
    }

}