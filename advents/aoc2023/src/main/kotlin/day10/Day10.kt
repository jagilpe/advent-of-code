package com.gilpereda.aoc2022.day10

import com.gilpereda.aoc2022.day10.Pipe.Companion.NE
import com.gilpereda.aoc2022.day10.Pipe.Companion.NS
import com.gilpereda.aoc2022.day10.Pipe.Companion.NW
import com.gilpereda.aoc2022.day10.Pipe.Companion.SE
import com.gilpereda.aoc2022.day10.Pipe.Companion.SW
import com.gilpereda.aoc2022.utils.*
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.geometry.Polygon
import com.gilpereda.aoc2022.utils.geometry.toPolygon
import kotlin.math.max

typealias PipesMap = TwoDimensionalMap<Pipe?>
typealias LoopMap = TwoDimensionalMap<PointState>

fun parseInput(sequence: Sequence<String>): Triple<Pipe, Point, PipesMap> {
    val inputList = sequence.toList()
    val rest = inputList.drop(1)
    val startPipe = Pipe.fromChar(inputList.first().first())!!
    val startPipePosition = rest.foldIndexed(Point(-1, -1)) { y, acc, next ->
        when (val x = next.indexOfFirst { it == 'S' }) {
            -1 -> acc
            else -> Point(x, y)
        }
    }
    if (startPipePosition.x < 0 || startPipePosition.y < 0) throw IllegalStateException("Could not find start point")

    val pipesMap = rest.parsed(startPipe)
    return Triple(startPipe, startPipePosition, pipesMap)
}

fun firstTask(input: Sequence<String>): String {
    val (startPipe, startPipePosition, pipesMap) = parseInput(input)
    val loopList = pipesMap.findLoop(startPipe, startPipePosition)
    return (loopList.size / 2 + loopList.size %2).toString()
}

/**
 * 342 too low
 */
fun secondTask(input: Sequence<String>): String {
    val (startPipe, startPipePosition, pipesMap) = parseInput(input)

    val loopList = pipesMap.findLoop(startPipe, startPipePosition)
    val polygon = loopList.toPolygon()
    val loopMap = pipesMap.findStates(loopList).fillOutsides()

    val filledMap = loopMap.fillInside(polygon)

    return filledMap.valuesToList().map { if (it is Inside) 1 else 0 }.sum().toString()
}

fun LoopMap.fillInside(polygon: Polygon): LoopMap =
    map2DValuesIndexed { point, value ->
        if (value is Unknown) {
            if (point inside polygon) Inside else Outside
        } else {
            value
        }
    }

fun PipesMap.findStates(loop: List<Point>): LoopMap =
    map2DValuesIndexed { point: Point, cell: Pipe? ->
        when {
            point in loop -> LoopPipe(pipe = cell!!)
            point.isBorder(height, width) -> Outside
            else -> Unknown
        }
    }

fun LoopMap.fillOutsides(): LoopMap =
    map2DValuesIndexed { x, y, pointState ->
        if (pointState is Unknown) {
            if (Point(x, y).isFree(this)) Outside else pointState
        } else pointState
    }

fun Point.isFree(loop: LoopMap): Boolean =
    isFreeToNorth(loop) || isFreeToSouth(loop) || isFreeToWest(loop) || isFreeToEast(loop)

private fun Point.isFreeToNorth(loop: LoopMap): Boolean =
    (0 until y).all { loop.get(Point(x, it)) !is LoopPipe }

private fun Point.isFreeToSouth(loop: LoopMap): Boolean =
    (y + 1 until loop.height).all { loop.get(Point(x, it)) !is LoopPipe }

private fun Point.isFreeToWest(loop: LoopMap): Boolean =
    (0 until x).all { loop.get(Point(it, y)) !is LoopPipe }

private fun Point.isFreeToEast(loop: LoopMap): Boolean =
    (x + 1 until loop.width).all { loop.get(Point(it, y)) !is LoopPipe }

fun List<Point>.nextPoint(pipesMap: PipesMap): Point {
    val previousPoint = get(1)
    val currentPoint = first()
    val pipe = pipesMap[currentPoint.y]!!.get(currentPoint.x)!!
    val (one, other) = pipe.next(currentPoint)
    return if (one == previousPoint) other else one
}

fun PipesMap.findLoop(startPipe: Pipe, startPosition: Point): List<Point> {
    tailrec fun go(acc: List<Point>): List<Point> =
        when (val next = acc.nextPoint(this)) {
            acc.last() -> acc
            else -> go(listOf(next) + acc)
        }

    val initial = listOf(startPipe.next(startPosition).first, startPosition)
    return go(initial)
}

fun List<String>.parsed(startPipe: Pipe): PipesMap =
    toTwoDimensionalMap { if (it == 'S') startPipe else Pipe.fromChar(it) }

fun <T> Map<Long, Map<Long, T>>.get(point: Point): T =
    get(point.y)!![point.x]!!

sealed interface PointState

data object Inside : PointState {
    override fun toString(): String = "I"
}

data object Outside : PointState {
    override fun toString(): String = "O"
}

data object Unknown : PointState {
    override fun toString(): String = "?"
}

data class LoopPipe(
    val pipe: Pipe,
) : PointState {
    override fun toString(): String = pipe.toString()
}

sealed interface Pipe {
    fun next(point: Point): Pair<Point, Point>

    companion object {
        data object NS : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.north(), point.south())

            override fun toString(): String = "|"
        }

        data object WE : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.west(), point.east())

            override fun toString(): String = "-"
        }

        data object NE : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.north(), point.east())

            override fun toString(): String = "L"
        }

        data object NW : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.north(), point.west())

            override fun toString(): String = "˩"
        }

        data object SW : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.south(), point.west())

            override fun toString(): String = "˥"
        }

        data object SE : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.south(), point.east())

            override fun toString(): String = "Γ"
        }

        fun fromChar(char: Char): Pipe? =
            when (char) {
                '|' -> NS
                '-' -> WE
                'L' -> NE
                'J' -> NW
                '7' -> SW
                'F' -> SE
                else -> null
            }
    }
}

fun dump(pipesMap: PipesMap): String = pipesMap.dump { pipe -> pipe?.toString() ?: "." }

@JvmName("booleanDump")
fun dump(loop: LoopMap): String =
    loop.dump { cell -> cell.toString() }

