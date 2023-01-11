package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class Fountain(show: Show, id: Int) : Effect(show, id) {

    override fun execute() {
        val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
        val material = if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
        val velocity =
            if (getSection().get("Velocity") != null)
                if(LocationUtils.getVectorFromString(getSection().getString("Velocity")!!) != null)
                    LocationUtils.getVectorFromString(getSection().getString("Velocity")!!)!!
                else Vector(0.0, 0.0, 0.0)
            else Vector(0.0, 0.0, 0.0)
        val length = if (getSection().get("Length") != null) getSection().getInt("Length") else 1
        val randomizer = if (getSection().get("Randomizer") != null) getSection().getDouble("Randomizer") / 10 else 0.0

        object: BukkitRunnable(){
            var c = 0
            override fun run() {
                if(c == length){
                    this.cancel()
                    return
                }

                val fallingBlock = location.world!!.spawnFallingBlock(location, material.createBlockData())
                fallingBlock.dropItem = false

                if(randomizer != 0.0)
                    fallingBlock.velocity = Vector(velocity.x + Math.random() * (randomizer * 2) - randomizer,
                        velocity.y + Math.random() * (randomizer * 2) - randomizer / 3,
                        velocity.z + Math.random() * (randomizer * 2) - randomizer)
                else
                    fallingBlock.velocity = velocity

                ShowUtils.addFallingBlock(fallingBlock)

                c++
            }
        }.runTaskTimer(EffectMaster.plugin, 0L, 1L)
    }

    override fun getType(): Type {
        return Type.FOUNTAIN
    }

    override fun isSync(): Boolean {
        return true
    }
}