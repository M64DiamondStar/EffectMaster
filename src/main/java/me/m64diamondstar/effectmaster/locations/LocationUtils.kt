package me.m64diamondstar.effectmaster.locations

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

object LocationUtils {

    /**
     * Get location from format (world, x, y, z[, yaw, pitch])
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
            args = string.split(" ")
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

    /**
     * Get location from format (world, x, y, z[, yaw, pitch])
     */
    fun getLocationFromString(string: String?, world: World): Location?{
        if(string == null)
            return null
        var args = string.split(", ")
        if(args.size == 1)
            args = string.split(",")
        if(args.size == 1)
            args = string.split(" - ")
        if(args.size == 1)
            args = string.split(" ")
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

        return Location(world, x, y, z)
    }

    /**
     * Get the location path from format (world, x1, y1, z1; x2, y2, z2; x3, y3, z4; ...)
     */
    fun getLocationPathFromString(string: String?): List<Location> {
        if(string == null)
            return emptyList()

        // List of locations as string
        val locationStrings = string.split("; ", ";")

        // Takes the world out of the first location
        val world = getLocationFromString(locationStrings[0])?.world ?: return emptyList()

        val locations = ArrayList<Location>()

        for (locationString in locationStrings){
            val location = getLocationFromString(locationString) ?: getLocationFromString(locationString, world)
            if (location != null) {
                locations.add(location)
            }
        }

        return locations
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

    fun getStringFromLocation(location: Location, asBlock: Boolean, withWorld: Boolean): String{
        return if(asBlock) {
            if (withWorld)
                "${location.world?.name}," +
                        " ${location.x.toInt()}," +
                        " ${location.y.toInt()}," +
                        " ${location.z.toInt()}"
            else
                "${location.x.toInt()}," +
                        " ${location.y.toInt()}," +
                        " ${location.z.toInt()}"
        }
        else {
            if (withWorld)
                "${location.world?.name}," +
                    " ${BigDecimal(location.x).setScale(3, RoundingMode.HALF_EVEN)}," +
                    " ${BigDecimal(location.y).setScale(3, RoundingMode.HALF_EVEN)}," +
                    " ${BigDecimal(location.z).setScale(3, RoundingMode.HALF_EVEN)}"
            else
                "${BigDecimal(location.x).setScale(3, RoundingMode.HALF_EVEN)}," +
                    " ${BigDecimal(location.y).setScale(3, RoundingMode.HALF_EVEN)}," +
                    " ${BigDecimal(location.z).setScale(3, RoundingMode.HALF_EVEN)}"
        }
    }

    fun calculateBezierPoint(controlPoints: List<Location>, t: Double): Location {
        if (controlPoints.size == 1) {
            return controlPoints[0].clone()
        }

        val nextPoints = ArrayList<Location>()
        for (i in 0 until controlPoints.size - 1) {
            val p0 = controlPoints[i]
            val p1 = controlPoints[i + 1]

            val x = (1 - t) * p0.x + t * p1.x
            val y = (1 - t) * p0.y + t * p1.y
            val z = (1 - t) * p0.z + t * p1.z

            nextPoints.add(Location(p0.world, x, y, z))
        }

        return calculateBezierPoint(nextPoints, t)
    }

}