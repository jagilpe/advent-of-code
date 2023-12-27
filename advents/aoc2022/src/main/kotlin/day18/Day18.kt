package com.gilpereda.aoc2022.day18


fun firstTask(input: Sequence<String>): String =
    LavaDroplet.parsed(input).faces.count().toString()

fun secondTask(input: Sequence<String>): String {
    val cover = Cover(input.parsed().toSet())
    cover.dump()
    return cover.faces.count().toString()
}

fun Sequence<String>.parsed(): Sequence<Cube> =
    filter(String::isNotBlank)
        .map(Cube::from)


class Cover(
    val cubes: Set<Cube>
) {
    private val minX: Int = cubes.minOf { it.x }
    private val maxX: Int = cubes.maxOf { it.x }
    private val minY: Int = cubes.minOf { it.y }
    private val maxY: Int = cubes.maxOf { it.y }
    private val minZ: Int = cubes.minOf { it.z }
    private val maxZ: Int = cubes.maxOf { it.z }

    private val xRange: IntRange = (minX - 2)..(maxX + 2)
    private val yRange: IntRange = (minY - 2)..(maxY + 2)
    private val zRange: IntRange = (minZ - 2)..(maxZ + 2)

    val faces: Set<Face> by lazy {
        positiveCubes.fold(emptySet()) { acc, cube ->
            val common = acc.intersect(cube.faces)
            acc + cube.faces - common
        }
    }

    private val negativeCubes: Set<Cube> by lazy {
        calculateCoverAsc() + calculateCoverDesc()
    }

    private val positiveCubes: Set<Cube> by lazy {
        zRange.flatMap { z ->
            yRange.flatMap { y ->
                xRange.map { x -> Cube(x, y, z) }
            }
        }.filter { it !in negativeCubes }.toSet()
    }

    private fun calculateCoverAsc(): Set<Cube> {
        tailrec fun go(acc: Set<Cube>, z: Int): Set<Cube> =
            if (z < maxZ) {
                val goUp = acc.cubesAt(z).goUp()
                val expand = goUp.expand()
                go(acc + expand, z + 1)
            } else {
                acc
            }

        return go(plateAt(zRange.first, xRange, yRange), zRange.first)
    }

    private fun calculateCoverDesc(): Set<Cube> {
        tailrec fun go(acc: Set<Cube>, z: Int): Set<Cube> =
            if (z > minZ) {
                go(acc + acc.cubesAt(z).goDown().expand(), z - 1)
            } else {
                acc
            }

        return go(plateAt(zRange.last, xRange, yRange), zRange.last)
    }

    private fun Set<Cube>.cubesAt(z: Int): Set<Cube> =
        filter { it.z == z }.toSet()

    private fun Set<Cube>.goUp(): Set<Cube> =
        map { it.copy(z = it.z + 1) }.filter { it !in cubes }.toSet()

    private fun Set<Cube>.goDown(): Set<Cube> =
        map { it.copy(z = it.z - 1) }.filter { it !in cubes }.toSet()

    private fun Set<Cube>.expand(): Set<Cube> {
        tailrec fun go(acc: Set<Cube>, rest: Set<Cube>): Set<Cube> =
            if (rest.isNotEmpty()) {
                val newCubes = rest.flatMap { it.neighboursInZ }
                    .filter { it !in acc && it !in cubes && it.x in xRange && it.y in yRange }.toSet()
                go(acc + newCubes, newCubes)
            } else {
                acc
            }

        return go(this, this)
    }

    private fun plateAt(z: Int, xRange: IntRange, yRange: IntRange): Set<Cube> =
        xRange.flatMap { x -> yRange.map { y -> Cube(x, y, z) } }.toSet()

    fun dump() {
        val string = zRange.joinToString("\n\n\n") { z ->
            yRange.joinToString("\n") { y ->
                positiveCubes.xLineOf(y, z) + "   " + cubes.xLineOf(y, z)
            }
        }
        println(string)
    }

    private fun Set<Cube>.xLineOf(y: Int, z: Int): String =
        xRange.map { x ->
            if (Cube(x, y, z) in this) 'o' else '.'
        }.joinToString("")

    private val Cube.neighboursInZ: List<Cube>
        get() = listOfNotNull(
            copy(x = x - 1),
            copy(y = y - 1),
            copy(y = y + 1),
            copy(x = x + 1),
        )
}


class LavaDroplet(
    val cubes: Set<Cube>
) {
    val faces: Set<Face> by lazy {
        cubes.fold(emptySet()) { acc, cube ->
            val common = acc.intersect(cube.faces)
            acc + cube.faces - common
        }
    }

    companion object {
        fun parsed(input: Sequence<String>): LavaDroplet =
            LavaDroplet(input.parsed().toSet())
    }
}

sealed interface Face {
    val x: Int
    val y: Int
    val z: Int

    val position: Int

    fun matches(other: Face): Boolean

    infix fun isConnectedWith(other: Face): Boolean

    fun mirrorOf(other: Face): Face?
}

data class XFace(
    override val x: Int,
    override val y: Int,
    override val z: Int,
) : Face {
    override val position: Int = x

    override fun matches(other: Face): Boolean =
        other is XFace && y == other.y && z == other.z

    override fun isConnectedWith(other: Face): Boolean =
        when (other) {
//            is XFace -> (x == other.x) && ((y == other.y && other.z in z - 1 .. z + 1) || (other.y in y - 1 .. y + 1 && other.z == z))
            is XFace -> false
            is YFace -> (other.x == x || other.x == x - 1) && (other.y == y || other.y == y + 1) && other.z == z
            is ZFace -> (other.x == x || other.x == x - 1) && other.y == y && (other.z == z || other.z == z + 1)
        }

    override fun mirrorOf(other: Face): Face? =
        when (other) {
            is XFace -> null
            is YFace -> if (other.x == x) other.copy(x = x - 1) else other.copy(x = x)
            is ZFace -> if (other.x == x) other.copy(x = x - 1) else other.copy(x = x)
        }
}

data class YFace(
    override val x: Int,
    override val y: Int,
    override val z: Int,
) : Face {
    override val position: Int = y
    override fun matches(other: Face): Boolean =
        other is YFace && x == other.x && y == other.y

    override fun isConnectedWith(other: Face): Boolean =
        when (other) {
            is XFace -> other.isConnectedWith(this)
            is YFace -> false
//            is YFace -> (y == other.y) && ((x == other.x && other.z in z - 1 .. z + 1) || (other.x in x - 1 .. x + 1 && other.z == z))
            is ZFace -> (other.y == y || other.y == y - 1) && other.x == x && (other.z == z || other.z == z + 1)
        }

    override fun mirrorOf(other: Face): Face? =
        when (other) {
            is XFace -> if (other.y == y) other.copy(y = y - 1) else other.copy(y = y)
            is YFace -> null
            is ZFace -> if (other.y == y) other.copy(y = y - 1) else other.copy(y = y)
        }
}

data class ZFace(
    override val x: Int,
    override val y: Int,
    override val z: Int,
) : Face {
    override val position: Int = z
    override fun matches(other: Face): Boolean =
        other is ZFace && x == other.x && z == other.z

    override fun isConnectedWith(other: Face): Boolean =
        when (other) {
            is XFace -> other.isConnectedWith(this)
            is YFace -> other.isConnectedWith(this)
//            is ZFace -> (z == other.z) && ((x == other.x && other.y in y - 1 .. y + 1) || (other.x in x - 1 .. x + 1 && other.y == y))
            is ZFace -> false
        }

    override fun mirrorOf(other: Face): Face? =
        when (other) {
            is XFace -> if (other.z == z) other.copy(z = z - 1) else other.copy(z = z)
            is YFace -> if (other.z == z) other.copy(z = z - 1) else other.copy(z = z)
            is ZFace -> null
        }
}


data class Cube(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    val faces: Set<Face> = setOf(
        XFace(x = x, y = y, z = z),
        XFace(x = x + 1, y = y, z = z),
        YFace(x = x, y = y, z = z),
        YFace(x = x, y = y + 1, z = z),
        ZFace(x = x, y = y, z = z),
        ZFace(x = x, y = y, z = z + 1),
    )

    companion object {
        fun from(line: String): Cube {
            val (x, y, z) = line.split(",")
            return Cube(x = x.toInt(), y = y.toInt(), z = z.toInt())
        }
    }
}