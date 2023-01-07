package me.m64diamondstar.effectmaster.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

object LocationUtils {

    /**
     * Get location from format (world, x, y, z[, yaw, pitch])
     *
     */
    fun getLocationFromString(string: String?): Location?{
        if(string == null)
            return null
        var args = string.split(", ")
        if(args.size == 1)
            args = string.split(",")
        if(args.size == 1)
            args = string.split(" - ")
        if(args.size == 1)
            return null

        val world = Bukkit.getWorld(args[0]) ?: return null
        val x: Double
        val y: Double
        val z: Double
        var yaw = 0F
        var pitch = 0F

        try{
            x = args[1].toDouble()
            y = args[2].toDouble()
            z = args[3].toDouble()
            if(args.size == 6){
                yaw = args[4].toFloat()
                pitch = args[5].toFloat()
            }
        }catch (e: NumberFormatException){
            return null
        }

        return Location(world, x, y, z, yaw, pitch)
    }

    fun getVectorFromString(string: String): Vector?{
        var args = string.split(", ")
        if(args.size == 1)
            args = string.split(",")
        if(args.size == 1)
            return null

        val x: Double
        val y: Double
        val z: Double

        try{
            x = args[0].toDouble()
            y = args[1].toDouble()
            z = args[2].toDouble()
        }catch (e: NumberFormatException){
            return null
        }

        return Vector(x, y, z)
    }

    fun getStringFromLocation(location: Location): String{
        return "${location.world?.name}," +
                " ${BigDecimal(location.x).setScale(3, RoundingMode.HALF_EVEN)}," +
                " ${BigDecimal(location.y).setScale(3, RoundingMode.HALF_EVEN)}," +
                " ${BigDecimal(location.z).setScale(3, RoundingMode.HALF_EVEN)}"
    }

}