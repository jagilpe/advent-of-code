package com.gilpereda.aoc2022.day22

import com.gilpereda.aoc2022.utils.geometry.Axis
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.geometry.Point3D
import com.gilpereda.aoc2022.utils.geometry.Point3D.Companion.CompareZ

/**
 * 404 too high
 */
fun firstTask(input: Sequence<String>): String {
    val tetris = Tetris3D(input.mapIndexed(Brick.Companion::parsed).toList())

    val canBeRemoved = tetris.canBeRemoved()
    return canBeRemoved.count().toString()
}

fun secondTask(input: Sequence<String>): String {
    val tetris = Tetris3D(input.mapIndexed(Brick.Companion::parsed).toList())

    return tetris.chainReaction().toString()
}

class Tetris3D(
    rawBricks: List<Brick>
) {
    private val sorted = rawBricks.sorted()

    private val bricks = sorted.compacted().sorted()

    private val body = bricks.flatMap { it.points }
        .let { points ->
            (points.minX..points.maxX).flatMap { x ->
                (points.minY..points.maxY).map { y ->
                    Point3D.from(x, y, -1)
                }
            }
        }.let { Brick(it, "body") }

    private val pointToBrick: Map<Point3D, Brick> =
        (bricks + body).flatMap { brick ->
            brick.points.map { it to brick }
        }.toMap()

    private fun List<Brick>.compacted(): List<Brick> =
        fold(emptyList()) { acc, brick ->
            val highestCompactedPoints = acc.highestPoints()
            acc + brick.compact(highestCompactedPoints)
        }

    private fun List<Brick>.brickCompacted(): Int =
        fold(0 to emptyList<Brick>()) { (compacted, acc), brick ->
            val highestCompactedPoints = acc.highestPoints()
            val compactedBrick = brick.compact(highestCompactedPoints)
            val newAcc = acc + compactedBrick
            val newCompacted = compacted + if(compactedBrick != brick) 1 else 0
            newCompacted to newAcc
        }.first

    fun canBeRemoved(): List<Brick> =
        bricks.filter { it.canBeRemoved() }

    fun chainReaction(): Int =
        bricks
            .mapIndexed { index, brick ->
                println("brick $index")
                brick to (bricks - brick).brickCompacted()
            }
            .sumOf { it.second }

    private fun Brick.bricksThatWouldFall(): Int {
        tailrec fun go(rest: List<Brick>, acc: Int = 0): Int =
            if (rest.isEmpty()) {
                acc
            } else {
                val wouldFall = rest.flatMap { brick ->
                    brick.supports().filter { it.wouldFallIfRemoved(brick) }
                }
                go(wouldFall, acc + wouldFall.size)
            }

        return go(listOf(this))
    }

    private fun Brick.canBeRemoved(): Boolean =
        supports()
            .none { it.wouldFallIfRemoved(this) }

    private fun Brick.wouldFallIfRemoved(supporting: Brick): Boolean =
        leansOn().none { it != supporting }

    private fun Brick.supports(): Set<Brick> =
        points.map { it.move(Axis.Z, 1) }
            .mapNotNull { pointToBrick[it] }
            .toSet()

    private fun Brick.leansOn(): Set<Brick> =
        points.map { it.move(Axis.Z, -1) }
            .mapNotNull { pointToBrick[it] }
            .filter { it != this }
            .toSet()
}



val List<Point3D>.minX
    get() = minOfOrNull { it.x } ?: 0
val List<Point3D>.maxX
    get() = maxOfOrNull { it.x } ?: 0
val List<Point3D>.minY
    get() = minOfOrNull { it.y } ?: 0
val List<Point3D>.maxY
    get() = maxOfOrNull { it.y } ?: 0
val List<Point3D>.minZ
    get() = minOfOrNull { it.z } ?: 0
val List<Point3D>.maxZ
    get() = maxOfOrNull { it.z } ?: 0

fun List<Brick>.highestPoints(): Map<Point, Int> =
    if (isEmpty()) {
        emptyMap()
    } else {
        val points = flatMap { it.points }
        (points.minX..points.maxX).flatMap { x ->
            (points.minY..points.maxY).map { y ->
                Point.from(x, y) to (points.filter { it.x == x && it.y == y }.maxOfOrNull { it.z } ?: -1)
            }
        }.toMap()
    }

data class Brick(
    val points: List<Point3D>,
    val name: String,
) : Comparable<Brick> {
    fun compact(highestPoints: Map<Point, Int>): Brick {
        val steps = points.filter { it.z == lowestPoint.z }
            .minOf { point -> point.z - (highestPoints[point.xy()] ?: -1) - 1 }

        return copy(points = points.map { it.move(Axis.Z, -steps) })
    }

    private val lowestPoint = points.sortedWith(CompareZ).first()

    companion object {
        fun parsed(index: Int, line: String): Brick {
            val (fromStr, toStr) = line.split("~")
            val from = Point3D.from(fromStr)
            val to = Point3D.from(toStr)
            return (from.x..to.x).flatMap { x ->
                (from.y..to.y).flatMap { y ->
                    (from.z..to.z).map { z ->
                        Point3D.from(x, y, z)
                    }
                }
            }
                .let {
                    Brick(
                        it,
                        "$index",
                    )
                }
        }
    }

    override fun compareTo(other: Brick): Int =
        CompareZ.compare(lowestPoint, other.lowestPoint)
}

fun List<Brick>.dump(): String {
    val pointToName = flatMapIndexed { index, brick -> brick.points.map { it to "${'A' + index}" } }.toMap()
    val points = pointToName.keys.toList()
    return (points.maxZ downTo 0).joinToString("\n") { z ->
        (points.minY..points.maxY).joinToString(" <-> ", prefix = "$z ") { y ->
            (points.minX..points.maxX).joinToString("") { x ->
                pointToName[Point3D.from(x, y, z)] ?: "."
            }
        }
    }
}

