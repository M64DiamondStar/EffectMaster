package me.m64diamondstar.effectmaster.locations

import me.m64diamondstar.effectmaster.utils.Quadruple
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
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
        }catch (_: NumberFormatException){
            return null
        }

        return Location(world, x, y, z, yaw, pitch)
    }

    fun getRelativeLocationFromString(string: String, relativeFrom: Location): Location? {
        val baseLocation = getLocationFromString(string) ?: return null

        // Ensure both locations are in the same world
        if (baseLocation.world != relativeFrom.world) {
            return null
        }

        val relativeX = baseLocation.x - relativeFrom.x
        val relativeY = baseLocation.y - relativeFrom.y
        val relativeZ = baseLocation.z - relativeFrom.z
        val relativeYaw = baseLocation.yaw - relativeFrom.yaw
        val relativePitch = baseLocation.pitch - relativeFrom.pitch

        return Location(relativeFrom.world, relativeX, relativeY, relativeZ, relativeYaw, relativePitch)
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
        }catch (_: NumberFormatException){
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

    fun getRelativePathFromString(string: String, relativeFrom: Location): List<Location> {
        val baseLocations = getLocationPathFromString(string)

        // If no base locations are found, return an empty list
        if (baseLocations.isEmpty()) {
            return emptyList()
        }

        val relativeLocations = ArrayList<Location>()

        for (baseLocation in baseLocations) {
            // Ensure both locations are in the same world
            if (baseLocation.world != relativeFrom.world) {
                return emptyList() // Or handle it differently if worlds don't match
            }

            val relativeX = baseLocation.x - relativeFrom.x
            val relativeY = baseLocation.y - relativeFrom.y
            val relativeZ = baseLocation.z - relativeFrom.z
            val relativeYaw = baseLocation.yaw - relativeFrom.yaw
            val relativePitch = baseLocation.pitch - relativeFrom.pitch

            // Add the relative location to the list
            relativeLocations.add(Location(relativeFrom.world, relativeX, relativeY, relativeZ, relativeYaw, relativePitch))
        }

        return relativeLocations
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
        }catch (_: NumberFormatException){
            return null
        }

        return Vector(x, y, z)
    }

    fun getStringFromLocation(location: Location?, asBlock: Boolean, withWorld: Boolean): String? {
        if(location == null) return null
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

    /**
     * Gets a list of all the values in the sequencer
     * @param value The sequencer value in the format of "ticks:width,height;ticks:width,height;..."
     */
    fun getDoubleSequencerValues(value: String): Map<Int, Triple<Double?, Double?, Material?>>?{
        try {
            val values = value.split(";")
            val list = mutableMapOf<Int, Triple<Double?, Double?, Material?>>()
            for (v in values) {
                val parts = v.split(":")
                if (parts.size == 2) {
                    val ticks = parts[0].replace(" ", "").toInt()
                    val whm = parts[1].split(",")
                    if (whm.size >= 2) {
                        val width = if(whm[0].replace(" ", "") == "~") null else whm[0].replace(" ", "").toDouble()
                        val height = if(whm[1].replace(" ", "") == "~") null else whm[1].replace(" ", "").toDouble()

                        if(whm.size == 3){
                            val material = if(Material.entries.toTypedArray()
                                    .contains(Material.valueOf(whm[2].replace(" ", "").uppercase())))
                                Material.valueOf(whm[2].replace(" ", "").uppercase())
                            else return null
                            list[ticks] = Triple(width, height, material)
                        }else
                            list[ticks] = Triple(width, height, null)
                    }
                }
            }
            return list
        }catch (_: kotlin.NumberFormatException) {
            return null
        }
    }

    /**
     * Gets a list of all the values in the sequencer
     * @param value The sequencer value in the format of "ticks:width,height,depth;ticks:width,height,depth;..."
     */
    fun getTripleSequencerValues(value: String): Map<Int, Quadruple<Double?, Double?, Double?, Material?>>?{
        try {
            val values = value.split(";")
            val list = mutableMapOf<Int, Quadruple<Double?, Double?, Double?, Material?>>()
            for (v in values) {
                val parts = v.split(":")
                if (parts.size == 2) {
                    val ticks = parts[0].replace(" ", "").toInt()
                    val whdm = parts[1].split(",")
                    if (whdm.size >= 3) {
                        val width = if(whdm[0].replace(" ", "") == "~") null else whdm[0].replace(" ", "").toDouble()
                        val height = if(whdm[1].replace(" ", "") == "~") null else whdm[1].replace(" ", "").toDouble()
                        val depth = if(whdm[2].replace(" ", "") == "~") null else whdm[2].replace(" ", "").toDouble()

                        if(whdm.size == 4){
                            val material = if(Material.entries.toTypedArray()
                                    .contains(Material.valueOf(whdm[3].replace(" ", "").uppercase())))
                                Material.valueOf(whdm[3].replace(" ", "").uppercase())
                                else return null

                            list[ticks] = Quadruple(width, height, depth, material)
                        }else
                            list[ticks] = Quadruple(width, height, depth, null)
                    }
                }
            }
            return list
        }catch (_: kotlin.NumberFormatException) {
            return null
        }
    }



}