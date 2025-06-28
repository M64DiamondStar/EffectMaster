package me.m64diamondstar.effectmaster.locations

import org.bukkit.Location

/**
 * Calculate a Bezier Point
 * @param controlPoints the list of locations/points
 * @param t the progression of the curve (has to be between 0 and 1)
 */
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

/**
 * Calculate a Catmull-Rom spline point from a list of control points.
 * @param path List of locations (must have at least 4 points)
 * @param t Value from 0.0 to 1.0 representing progression along the full path
 */
fun calculateCatmullRomPoint(path: List<Location>, t: Double): Location {
    if (path.size < 4) throw IllegalArgumentException("At least 4 control points are required")
    val padded = listOf(path.first()) + path + listOf(path.last())

    // Clamp t to [0, 1]
    val clampedT = t.coerceIn(0.0, 1.0)

    // Total number of spline segments
    val segmentCount = padded.size - 3

    // Map t [0,1] to segment index and local segment t
    val totalT = clampedT * segmentCount
    val segmentIndex = totalT.toInt().coerceAtMost(segmentCount - 1)
    val localT = totalT - segmentIndex

    val p0 = padded[segmentIndex]
    val p1 = padded[segmentIndex + 1]
    val p2 = padded[segmentIndex + 2]
    val p3 = padded[segmentIndex + 3]

    return interpolateCatmullRom(p0, p1, p2, p3, localT)
}

private fun interpolateCatmullRom(p0: Location, p1: Location, p2: Location, p3: Location, t: Double): Location {
    val t2 = t * t
    val t3 = t2 * t

    val x = 0.5 * (
            2 * p1.x +
                    (p2.x - p0.x) * t +
                    (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
                    (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3
            )

    val y = 0.5 * (
            2 * p1.y +
                    (p2.y - p0.y) * t +
                    (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 +
                    (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3
            )

    val z = 0.5 * (
            2 * p1.z +
                    (p2.z - p0.z) * t +
                    (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2 +
                    (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3
            )

    return Location(p1.world, x, y, z)
}

/**
 * Calculate a Polygonal Chain Point
 * @param controlPoints the list of locations/points
 * @param t the progression of the chain (has to be between 0 and 1)
 */
fun calculatePolygonalChain(controlPoints: List<Location>, t: Double): Location {
    val n = controlPoints.size - 1
    val totalLength = calculateTotalLength(controlPoints)
    val equidistantT = t * totalLength

    var currentLength = 0.0
    var segment = 0
    while (segment < n - 1 && currentLength + controlPoints[segment].distance(controlPoints[segment + 1]) < equidistantT) {
        currentLength += controlPoints[segment].distance(controlPoints[segment + 1])
        segment++
    }

    val tSegment = (equidistantT - currentLength) / controlPoints[segment].distance(controlPoints[segment + 1])

    val p0 = controlPoints[segment]
    val p1 = controlPoints[segment + 1]

    val x = p0.x + (p1.x - p0.x) * tSegment
    val y = p0.y + (p1.y - p0.y) * tSegment
    val z = p0.z + (p1.z - p0.z) * tSegment

    return Location(controlPoints[0].world, x, y, z)
}

private fun calculateTotalLength(controlPoints: List<Location>): Double {
    var totalLength = 0.0
    for (i in 0 until controlPoints.size - 1) {
        totalLength += controlPoints[i].distance(controlPoints[i + 1])
    }
    return totalLength
}