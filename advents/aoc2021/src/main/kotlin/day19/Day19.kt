package com.gilpereda.adventsofcode.adventsofcode2021.day19

import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day19.Coord.Companion.ORIGIN
import kotlin.math.absoluteValue
import kotlin.math.pow


val part1: Executable = { input ->
    getTransformations(input)
        .flatMap { (scanner, transformationList) ->
            scanner.beacons.map { transformationList.fold(it) { beacon, transformation -> transformation.transform(beacon) } }
        }
        .distinct().count().toString()
}



val part2: Executable = { input ->
    val coords = getTransformations(input)
        .map { (_, transformations) ->
            transformations.fold(Coord(0.0, 0.0,0.0 )) { coord, transformation -> transformation.transform(coord) }
        }

    coords.flatMap { coord ->
        coords.map { other -> coord.manhattanDistanceTo(other) }
    }.maxOrNull()!!.toString()
}

fun getTransformations(input: Sequence<String>): List<Pair<Scanner, List<Transformation>>> {
    val scanners = parseInput(input.toList())

    val transformationMap = scanners.associateWith { scanner -> scanner.transformationsTo(scanners) }

    val refScanner = scanners.first()

    return scanners.map { scanner -> scanner to scanner.transformationToRef(refScanner, transformationMap) }
}

fun parseInput(input: List<String>): List<Scanner> {
    fun go(seq: List<String>, scanners: List<Scanner>): List<Scanner> =
        if (seq.iterator().hasNext()) {
            val rawScanner = seq.takeWhile { it.isNotBlank() }.toList()
            go(seq.drop(rawScanner.size + 1), scanners + parseScanner(rawScanner))
        } else {
            scanners
        }

    return go(input, emptyList())
}

private val idRegex = "--- scanner (\\d+) ---".toRegex()
private fun parseScanner(input: List<String>): Scanner {
    val id = idRegex.find(input.first())?.destructured
        ?.let { (idStr) -> idStr.toInt() }
        ?: throw Exception("Could not parse the scanner's id: ${input.first()}")
    val beacons = input.drop(1).map(::parseBeacon)

    return Scanner(id = id, beacons = beacons)
}

private val beaconRegex = "([\\-0-9]+),([\\-0-9]+),([\\-0-9]+)".toRegex()
private fun parseBeacon(input: String): Beacon =
    beaconRegex.find(input)?.destructured
        ?.let { (x, y, z) -> Beacon(Coord(x = x.toDouble(), y = y.toDouble(), z = z.toDouble())) }
        ?: throw Exception("Could not parse beacon: $input")

data class Scanner(val id: Int, val beacons: List<Beacon>, val coordinates: Coord = ORIGIN) {
    fun commonBeacons(otherScanner: Scanner): List<Pair<Beacon,Beacon>> =
        distances.mapNotNull { (thisBeacon, beaconDistances) ->
            otherScanner.getMatchingBeacon(beaconDistances)?.let { otherBeacon -> Pair(thisBeacon, otherBeacon) }
        }

    private val distances: Map<Beacon, List<Long>>
        get() = beacons.associateWith { beacon ->
            beacons.filter { it != beacon }.map { beacon.distanceTo(it) }.sorted()
        }

    private fun getMatchingBeacon(otherDistances: List<Long>): Beacon? =
        distances.map { (beacon, beaconDistances) -> beacon to beaconDistances.filter { it in otherDistances } }
            .filter { it.second.size > 3 }
            .maxByOrNull { it.second.size }?.first
}

data class Beacon(val coord: Coord) {
    override fun toString(): String = "[ ${coord.x}, ${coord.y}, ${coord.z} ]"
}

fun Scanner.transformationsTo(scanners: List<Scanner>): Map<Scanner, Transformation> =
    scanners
        .associateWith { other -> this.commonBeacons(other) }
        .filterValues { it.size > 10 }
        .mapValues { (_, beacons) -> Transformation.fromPoints(beacons.coords) }

data class Distance(val beacon: Beacon, val distance: Long)

val List<Pair<Beacon, Beacon>>.coords: List<Pair<Coord, Coord>>
    get() = map { (one, other) -> one.coord to other.coord }

fun Beacon.distanceTo(other: Beacon): Long =
    ((coord.x - other.coord.x).toFloat().pow(2) + (coord.y - other.coord.y).toFloat().pow(2) + (coord.z - other.coord.z).toFloat().pow(2)).toLong()

data class Coord(val x: Double, val y: Double, val z: Double) {
    fun manhattanDistanceTo(other: Coord): Double =
        (x - other.x).absoluteValue +
                (y - other.y).absoluteValue +
                (z - other.z).absoluteValue

    companion object {
        val ORIGIN = Coord(0.0, 0.0, 0.0)
    }
}

fun Scanner.transformationToRef(refScanner: Scanner, transformations: Map<Scanner, Map<Scanner, Transformation>>): List<Transformation> {
    tailrec fun go(transformationPairs: List<Pair<Scanner, List<Transformation>>>): List<Transformation> {
        val transformationList = transformationPairs
            .firstOrNull { it.first == refScanner }?.second
        return if (transformationList != null) {
            transformationList
        } else {
            val newTransformationPairs = transformationPairs.flatMap { (scanner, list) -> transformations[scanner]!!.map { it.key to list + it.value } }

            go(newTransformationPairs)
        }
    }
    return go (transformations[this]!!.map { it.key to listOf(it.value) })
}

fun Transformation.transform(beacon: Beacon): Beacon =
    Beacon(transform(coord = beacon.coord))

typealias Origin = Coord
typealias Destination = Coord

data class Transformation(
    val a11: Double,
    val a12: Double,
    val a13: Double,
    val a14: Double,
    val a21: Double,
    val a22: Double,
    val a23: Double,
    val a24: Double,
    val a31: Double,
    val a32: Double,
    val a33: Double,
    val a34: Double,
) {
    fun transform(coord: Coord): Coord =
        Coord(
            x = coord.x * a11 + coord.y * a12 + coord.z * a13 + a14,
            y = coord.x * a21 + coord.y * a22 + coord.z * a23 + a24,
            z = coord.x * a31 + coord.y * a32 + coord.z * a33 + a34,
        )

    override fun toString(): String = """
        |   $a11    $a12    $a13    $a14    |
        |   $a21    $a22    $a23    $a24    |
        |   $a31    $a32    $a33    $a34    |
    """.trimIndent()

    companion object {
        fun fromPoints(points: List<Pair<Origin, Destination>>): Transformation {
            assert(points.size > 3)
            return fromPoints(points[0], points[1], points[2], points[3], )
        }

        fun fromPoints(
            p1: Pair<Origin, Destination>,
            p2: Pair<Origin, Destination>,
            p3: Pair<Origin, Destination>,
            p4: Pair<Origin, Destination>,
        ): Transformation {
            val auxA1 = ((p3.first.x - p1.first.x) * (p1.first.y - p2.first.y) - (p2.first.x - p1.first.x) * (p1.first.y - p3.first.y))
            val auxA12 = (p2.first.x - p1.first.x) * (p3.second.x - p1.second.x) - (p3.first.x - p1.first.x) * (p2.second.x - p1.second.x)
            val auxA13 = ((p2.first.x - p1.first.x) * (p1.first.z - p3.first.z) - (p3.first.x - p1.first.x) * (p1.first.z - p2.first.z))
            val auxA14 = ((p4.first.x - p1.first.x) * (p1.first.y - p2.first.y) - (p2.first.x - p1.first.x) * (p1.first.y - p4.first.y))
            val auxA15 = (p2.first.x - p1.first.x) * (p4.second.x - p1.second.x) - (p4.first.x - p1.first.x) * (p2.second.x - p1.second.x)
            val auxA16 = ((p2.first.x - p1.first.x) * (p1.first.z - p4.first.z) - (p4.first.x - p1.first.x) * (p1.first.z - p2.first.z))

            val a13 = (auxA1 * auxA15 - auxA12 * auxA14) / (auxA13 * auxA14 - auxA1* auxA16)
            val a12 = (auxA12 + auxA13 * a13) / auxA1
            val a11 = (p2.second.x - p1.second.x + a12 * (p1.first.y - p2.first.y) + a13 * (p1.first.z - p2.first.z)) / (p2.first.x - p1.first.x)
            val a14 = p1.second.x - a11 * p1.first.x - a12 * p1.first.y - a13 * p1.first.z

            val auxA22 = (p2.first.x - p1.first.x) * (p3.second.y - p1.second.y) - (p3.first.x - p1.first.x) * (p2.second.y - p1.second.y)
            val auxA23 = ((p2.first.x - p1.first.x) * (p1.first.z - p3.first.z) - (p3.first.x - p1.first.x) * (p1.first.z - p2.first.z))
            val auxA24 = ((p4.first.x - p1.first.x) * (p1.first.y - p2.first.y) - (p2.first.x - p1.first.x) * (p1.first.y - p4.first.y))
            val auxA25 = (p2.first.x - p1.first.x) * (p4.second.y - p1.second.y) - (p4.first.x - p1.first.x) * (p2.second.y - p1.second.y)
            val auxA26 = ((p2.first.x - p1.first.x) * (p1.first.z - p4.first.z) - (p4.first.x - p1.first.x) * (p1.first.z - p2.first.z))

            val a23 = (auxA1 * auxA25 - auxA22 * auxA24) / (auxA23 * auxA24 - auxA1* auxA26)
            val a22 = (auxA22 + auxA23 * a23) / auxA1
            val a21 = (p2.second.y - p1.second.y + a22 * (p1.first.y - p2.first.y) + a23 * (p1.first.z - p2.first.z)) / (p2.first.x - p1.first.x)
            val a24 = p1.second.y - a21 * p1.first.x - a22 * p1.first.y - a23 * p1.first.z

            val auxA32 = (p2.first.x - p1.first.x) * (p3.second.z - p1.second.z) - (p3.first.x - p1.first.x) * (p2.second.z - p1.second.z)
            val auxA33 = ((p2.first.x - p1.first.x) * (p1.first.z - p3.first.z) - (p3.first.x - p1.first.x) * (p1.first.z - p2.first.z))
            val auxA34 = ((p4.first.x - p1.first.x) * (p1.first.y - p2.first.y) - (p2.first.x - p1.first.x) * (p1.first.y - p4.first.y))
            val auxA35 = (p2.first.x - p1.first.x) * (p4.second.z - p1.second.z) - (p4.first.x - p1.first.x) * (p2.second.z - p1.second.z)
            val auxA36 = ((p2.first.x - p1.first.x) * (p1.first.z - p4.first.z) - (p4.first.x - p1.first.x) * (p1.first.z - p2.first.z))

            val a33 = (auxA1 * auxA35 - auxA32 * auxA34) / (auxA33 * auxA34 - auxA1* auxA36)
            val a32 = (auxA32 + auxA33 * a33) / auxA1
            val a31 = (p2.second.z - p1.second.z + a32 * (p1.first.y - p2.first.y) + a33 * (p1.first.z - p2.first.z)) / (p2.first.x - p1.first.x)
            val a34 = p1.second.z - a31 * p1.first.x - a32 * p1.first.y - a33 * p1.first.z

            return Transformation(
                a11 = a11,
                a12 = a12,
                a13 = a13,
                a14 = a14,
                a21 = a21,
                a22 = a22,
                a23 = a23,
                a24 = a24,
                a31 = a31,
                a32 = a32,
                a33 = a33,
                a34 = a34,
            )
        }
    }
}