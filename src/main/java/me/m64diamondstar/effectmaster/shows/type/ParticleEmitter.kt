package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
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
            val length = if (getSection(effectShow, id).get("Length") != null) getSection(effectShow, id).getInt("Length") else 1
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
                            if(c == length){
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

                            if(c == length){
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

                            if(c == length){
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
                            if(c == length){
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

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "PARTICLE_EMITTER"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Particle", "SMOKE_NORMAL"))
        list.add(Pair("Color", "0, 0, 0"))
        list.add(Pair("Block", "STONE"))
        list.add(Pair("Amount", 1))
        list.add(Pair("dX", 1))
        list.add(Pair("dY", 1))
        list.add(Pair("dZ", 1))
        list.add(Pair("Force", false))
        list.add(Pair("Length", 20))
        list.add(Pair("StartUp", 0))
        list.add(Pair("Delay", 0))
        return list
    }

}