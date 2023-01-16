package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class ParticleEmitter(show: Show, private val id: Int) : Effect(show, id) {

    init{
        getShow().getConfig().set("$id.Type", "Particle-Emitter")
    }

    override fun execute() {
        try{
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
            val particle = getSection().getString("Particle")?.let { Particle.valueOf(it.uppercase()) } ?: return
            val amount = if (getSection().get("Amount") != null) getSection().getInt("Amount") else 0
            val dX = if (getSection().get("dX") != null) getSection().getDouble("dX") else 0.0
            val dY = if (getSection().get("dY") != null) getSection().getDouble("dY") else 0.0
            val dZ = if (getSection().get("dZ") != null) getSection().getDouble("dZ") else 0.0
            val length = if (getSection().get("Length") != null) getSection().getInt("Length") else 1
            val force = if (getSection().get("Force") != null) getSection().getBoolean("Force") else false
            val extra = if(amount == 0) 1.0 else 0.0

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

                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {
                            if(c == length){
                                this.cancel()
                                return
                            }
                            location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin, 0L, 1L)
                }

                Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                    val material =
                        if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!.uppercase()) else Material.STONE
                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {

                            if(c == length){
                                this.cancel()
                                return
                            }
                            location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, material.createBlockData(), force)
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin, 0L, 1L)
                }

                Particle.ITEM_CRACK -> {
                    val material =
                        if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!.uppercase()) else Material.STONE
                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {

                            if(c == length){
                                this.cancel()
                                return
                            }
                            location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, ItemStack(material), force)
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin, 0L, 1L)
                }

                else -> {
                    object: BukkitRunnable(){
                        var c = 0
                        override fun run() {
                            if(c == length){
                                this.cancel()
                                return
                            }
                            location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null, force)
                            c++
                        }
                    }.runTaskTimerAsynchronously(EffectMaster.plugin, 0L, 1L)
                }
            }
        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The particle you entered doesn't exist. Please choose a valid type.")
        }
    }

    override fun getType(): Type {
        return Type.PARTICLE_EMITTER
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
        list.add(Pair("Delay", 0))
        return list
    }

}