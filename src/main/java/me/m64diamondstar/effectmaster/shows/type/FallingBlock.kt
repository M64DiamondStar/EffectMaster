package me.m64diamondstar.effectmaster.shows.type

import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.LocationUtils
import org.bukkit.Material
import org.bukkit.util.Vector

class FallingBlock(effectShow: EffectShow, id: Int) : Effect(effectShow, id) {

    override fun execute() {

        val location = LocationUtils.getLocationFromString(getSection().getString("Location")!!) ?: return
        val material = if (getSection().get("Block") != null) Material.valueOf(getSection().getString("Block")!!.uppercase()) else Material.STONE
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

    override fun getDefaults(): List<Pair<String, Any>> {
        val list = ArrayList<Pair<String, Any>>()
        list.add(Pair("Type", "FALLING_BLOCK"))
        list.add(Pair("Location", "world, 0, 0, 0"))
        list.add(Pair("Velocity", "0, 0, 0"))
        list.add(Pair("Block", "STONE"))
        list.add(Pair("Delay", 0))
        return list
    }
}