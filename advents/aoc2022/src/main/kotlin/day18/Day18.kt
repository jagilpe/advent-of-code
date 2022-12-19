package com.gilpereda.aoc2022.day18


fun firstTask(input: Sequence<String>): String =
    LavaDroplet.parsed(input).faces.count().toString()

fun secondTask(input: Sequence<String>): String {
    val lavaDroplet = LavaDroplet.parsed(input)
//    lavaDroplet.dumpAll()
//    lavaDroplet.dumpOuter()
    return (lavaDroplet.allOuterFaces.count()).toString()
}

fun Sequence<String>.parsed(): Sequence<Cube> =
    filter(String::isNotBlank)
        .map(Cube::from)

class LavaDroplet(
    private val cubes: Set<Cube>
) {
    val faces: Set<Face> by lazy {
        cubes.fold(emptySet()) { acc, cube ->
            val common = acc.intersect(cube.faces)
            acc + cube.faces - common
        }
    }

    val allOuterFaces: Set<Face> by lazy {
        outerFaces + otherOuterFaces
    }

    private val otherOuterFaces: Set<Face> by lazy {
        faces.filter { it !in outerFaces }
            .filter { face ->
                outerFaces.any {
                val mirrorFace = face.mirrorOf(it)
                it.isConnectedWith(face) && mirrorFace !in outerFaces
            } }
            .toSet()
    }

    val outerFaces: Set<Face> by lazy {
        outerXFaces + outerYFaces + outerZFaces
    }

    private val xFaces: Set<XFace> by lazy { faces.filterIsInstance<XFace>().toSet() }
    private val yFaces: Set<YFace> by lazy { faces.filterIsInstance<YFace>().toSet() }
    private val zFaces: Set<ZFace> by lazy { faces.filterIsInstance<ZFace>().toSet() }

    private val outerXFaces: Set<XFace> by lazy {
        getOuter { it.x }
    }

    private val outerYFaces: Set<YFace> by lazy {
        getOuter { it.y }
    }

    private val outerZFaces: Set<ZFace> by lazy {
        getOuter { it.z }
    }

    private inline fun <reified T : Face> getOuter(position: (T) -> Int): Set<T> =
        faces.filterIsInstance(T::class.java)
            .filter { face ->
                matching(face).partition { position(face) < position(it) }.let { (ones, others) ->
                    ones.isEmpty() || others.isEmpty()
                }
            }
            .toSet()

    private inline fun <reified T : Face> matching(face: T): List<T> =
        faces.filterIsInstance(T::class.java).filter {
            face != it && when (face) {
                is XFace -> face.y == it.y && face.z == it.z
                is YFace -> face.x == it.x && face.z == it.z
                is ZFace -> face.x == it.x && face.y == it.y
                else -> false
            }
        }

    fun dumpAll() {
        dump(xFaces, yFaces, zFaces)
    }

    fun dumpOuter() {
        dump(outerXFaces, outerYFaces, outerZFaces)
    }

    private fun dump(xFaces: Set<XFace>, yFaces: Set<YFace>, zFaces: Set<ZFace>) {
        println("X axis")
        println(dumpXFaces(xFaces))
        println()

        println("Y axis")
        println(dumpYFaces(yFaces))
        println()

        println("Z axis")
        println(dumpZFaces(zFaces))
        println()
    }

    private fun dumpXFaces(faces: Set<Face>): String {
        val minX = faces.minOf { it.x }
        val maxX = faces.maxOf { it.x }
        val minY = faces.minOf { it.y }
        val maxY = faces.maxOf { it.y }
        val minZ = faces.minOf { it.z }
        val maxZ = faces.maxOf { it.z }

        return (minX..maxX).joinToString("\n\n") { x ->
            (maxZ downTo minZ).joinToString("\n") { z ->
                (minY..maxY).joinToString("") { y ->
                    if (XFace(x, y, z) in faces) "o" else "."
                }
            }
        }
    }

    private fun dumpYFaces(faces: Set<Face>): String {
        val minX = faces.minOf { it.x }
        val maxX = faces.maxOf { it.x }
        val minY = faces.minOf { it.y }
        val maxY = faces.maxOf { it.y }
        val minZ = faces.minOf { it.z }
        val maxZ = faces.maxOf { it.z }

        return (minY..maxY).joinToString("\n\n") { y ->
            (maxZ downTo minZ).joinToString("\n") { z ->
                (minX..maxX).joinToString("") { x ->
                    if (YFace(x, y, z) in faces) "o" else "."
                }
            }
        }
    }

    private fun dumpZFaces(faces: Set<Face>): String {
        val minX = faces.minOf { it.x }
        val maxX = faces.maxOf { it.x }
        val minY = faces.minOf { it.y }
        val maxY = faces.maxOf { it.y }
        val minZ = faces.minOf { it.z }
        val maxZ = faces.maxOf { it.z }

        return (minZ..maxZ).joinToString("\n\n") { z ->
            (maxY downTo minY).joinToString("\n") { y ->
                (minX..maxX).joinToString("") { x ->
                    if (ZFace(x, y, z) in faces) "o" else "."
                }
            }
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
        when(other) {
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
        when(other) {
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
        when(other) {
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