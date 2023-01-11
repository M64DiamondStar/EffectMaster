package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Show
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Material
import org.bukkit.util.Vector

class FallingBlock(show: Show, id: Int) : Effect(show, id) {

    override fun execute() {

        val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
        val material = if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!) else Material.STONE
        val velocity =
            if (getSection().get("Velocity") != null)
                if(LocationUtils.getVectorFromString(getSection().getString("Velocity")!!) != null)
                    LocationUtils.getVectorFromString(getSection().getString("Velocity")!!)!!
                else Vector(0.0, 0.0, 0.0)
            else Vector(0.0, 0.0, 0.0)

        val fallingBlock = location.world!!.spawnFallingBlock(location, material.createBlockData())
        fallingBlock.velocity = velocity
        fallingBlock.dropItem = false

        ShowUtils.addFallingBlock(fallingBlock)

    }

    override fun getType(): Type {
        return Type.FALLING_BLOCK
    }

    override fun isSync(): Boolean {
        return true
    }
}