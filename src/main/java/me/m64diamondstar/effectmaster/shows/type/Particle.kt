package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.EffectType
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack

class Particle(show: Show, private val id: Int) : EffectType(show, id) {

    init{
        getShow().getConfig().set("$id.Type", "Particle")
    }

    override fun execute() {
        try {
            val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
            val particle = getSection().getString("Particle")?.let { Particle.valueOf(it) } ?: return
            val amount = if (getSection().get("Amount") != null) getSection().getInt("Amount") else 0
            val dX = if (getSection().get("dX") != null) getSection().getDouble("dX") else 0.0
            val dY = if (getSection().get("dY") != null) getSection().getDouble("dY") else 0.0
            val dZ = if (getSection().get("dZ") != null) getSection().getDouble("dZ") else 0.0
            val force = if (getSection().get("Force") != null) getSection().getBoolean("Force") else false
            val extra = if (amount == 0) 1.0 else 0.0


            when (particle) {
                Particle.REDSTONE, Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT -> {
                    val color = Colors.getJavaColorFromString(getSection().getString("Color")!!)
                    val dustOptions = Particle.DustOptions(
                        Color.fromRGB(color.red, color.green, color.blue),
                        if (getSection().get("Size") != null)
                            getSection().getInt("Size").toFloat()
                        else
                            1F
                    )
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, dustOptions, force)
                }

                Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                    val material =
                        if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
                    location.world!!.spawnParticle(
                        particle, location, amount, dX, dY, dZ, extra, material.createBlockData(), force
                    )
                }

                Particle.ITEM_CRACK -> {
                    val material =
                        if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
                    location.world!!.spawnParticle(
                        particle, location, amount, dX, dY, dZ, extra, ItemStack(material), force
                    )
                }

                else -> {
                    location.world!!.spawnParticle(particle, location, amount, dX, dY, dZ, extra, null, force)
                }
            }
        }catch (ex: IllegalArgumentException){
            EffectMaster.plugin.logger.warning("Couldn't play effect with ID $id from ${getShow().getName()} in category ${getShow().getCategory()}.")
            EffectMaster.plugin.logger.warning("The particle you entered doesn't exist. Please choose a valid type.")
        }
    }

    override fun getType(): Types {
        return Types.PARTICLE
    }

    override fun isSync(): Boolean {
        return false
    }

}