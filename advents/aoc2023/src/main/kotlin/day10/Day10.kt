package com.gilpereda.aoc2022.day10

import com.gilpereda.aoc2022.utils.*

typealias PipesMap = Map<Int, Map<Int, Pipe?>>
typealias LoopMap = Map<Int, Map<Int, PointState>>

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
    tailrec fun maxDistance(paths: Pair<List<Point>, List<Point>>): Int =
        if (paths.finished) {
            paths.first.size - 1
        } else {
            maxDistance(Pair(paths.first.nextList(pipesMap), paths.second.nextList(pipesMap)))
        }

    val (one, other) = startPipe.next(startPipePosition)
    return maxDistance(Pair(listOf(one, startPipePosition), listOf(other, startPipePosition))).toString()
}

fun secondTask(input: Sequence<String>): String {
    val (startPipe, startPipePosition, pipesMap) = parseInput(input)

    val loopMap = pipesMap.findLoop(startPipe, startPipePosition)
        .let { it.fillOutsides() }
        .let { it.fillPipeOutsideOne() }
        .let { it.fillLoopInformation() }
        .let { it.findInsidePoints() }

    TODO()
//    fun go(acc: LoopMap, queue: List<Point>): LoopMap =
//        if (acc.finished) {
//            acc
//        } else {
//            TODO()
//        }
//    return loopMap.values.flatMapIndexed { y, line ->
//        List(line.values.size) { x -> loopMap.isInLoop(Point(x, y)) }
//    }.count().toString()
}

//fun Loop.surrounded(point: Point, orientation: Orientation): Boolean {
//    val pointsToEndOfMap: List<Point> = when (orientation) {
//        Orientation.NORTH -> (0 until point.y).map { y -> Point(point.x, y) }
//        Orientation.SOUTH -> (point.y + 1 until height).map { y -> Point(point.x, y) }
//        Orientation.WEST -> (0 until point.x).map { x -> Point(x, point.y) }
//        Orientation.EAST -> (point.x + 1 until width).map { x -> Point(x, point.y) }
//    }
//    return pointsToEndOfMap.count { get(it) } % 2 == 1
//}

val <T> Map<Int, Map<Int, T>>.width: Int
    get() = values.first().size

val <T> Map<Int, Map<Int, T>>.height: Int
    get() = values.size

fun List<Point>.nextList(pipesMap: PipesMap): List<Point> =
    listOf(nextPoint(pipesMap)) + this

fun List<Point>.nextPoint(pipesMap: PipesMap): Point {
    val previousPoint = get(1)
    val currentPoint = first()
    val pipe = pipesMap[currentPoint.y]!!.get(currentPoint.x)!!
    val (one, other) = pipe.next(currentPoint)
    return if (one == previousPoint) other else one
}

val Pair<List<Point>, List<Point>>.finished: Boolean
    get() = first.first() == second.first() || first.first() == second[1]

fun PipesMap.findLoop(startPipe: Pipe, startPosition: Point): LoopMap {
    tailrec fun go(acc: List<Point>): List<Point> =
        when (val next = acc.nextPoint(this)) {
            acc.last() -> acc
            else -> go(listOf(next) + acc)
        }

    val initial = listOf(startPipe.next(startPosition).first, startPosition)
    val loopList = go(initial)
    return mapValues { (y, line) ->
        line.mapValues { (x, pipe) ->
            val point = Point(x, y)
            when {
                point in loopList -> Loop.fromPipe(pipe!!)
                point.isBorder(height, width) -> Outside
                else -> Unknown
            }
        }
    }
}

fun LoopMap.findInsidePoints(): LoopMap {
    tailrec fun go(acc: LoopMap): LoopMap =
        if (acc.allMatch { it !is Unknown }) {
            acc
        } else {
            val newMap = map2DValues { x, y, point ->
                if (point is Unknown) {
                    point.let {
                        getNullable(x, y + 1)
                            ?.let { southNeighbour ->
                                when (southNeighbour) {
                                    is Loop -> when (val isOutside = southNeighbour.isNorthOutside) {
                                        null -> it
                                        else -> if (isOutside) Outside else Inside
                                    }

                                    is Inside -> Inside
                                    is Outside -> Outside
                                    else -> it
                                }
                            } ?: it
                    }.let {
                        getNullable(x, y - 1)
                            ?.let { northNeighbour ->
                                when (northNeighbour) {
                                    is Loop -> when (val isOutside = northNeighbour.isSouthOutside) {
                                        null -> it
                                        else -> if (isOutside) Outside else Inside
                                    }

                                    is Inside -> Inside
                                    is Outside -> Outside
                                    else -> it
                                }
                            }
                            ?: it
                    }.let {
                        getNullable(x - 1, y)
                            ?.let { westNeighbour ->
                                when (westNeighbour) {
                                    is Loop -> when (val isOutside = westNeighbour.isEastOutside) {
                                        null -> it
                                        else -> if (isOutside) Outside else Inside
                                    }

                                    is Inside -> Inside
                                    is Outside -> Outside
                                    else -> it
                                }
                            }
                            ?: it
                    }.let {
                        getNullable(x + 1, y)
                            ?.let { eastNeighbour ->
                                when (eastNeighbour) {
                                    is Loop -> when (val isOutside = eastNeighbour.isWestOutside) {
                                        null -> it
                                        else -> if (isOutside) Outside else Inside
                                    }

                                    is Inside -> Inside
                                    is Outside -> Outside
                                    else -> it
                                }
                            }
                            ?: it
                    }
                } else {
                    point
                }
            }
            go(newMap)
        }
    return go(this)
}

fun LoopMap.fillOutsides(): LoopMap =
    map2DValues { x, y, pointState ->
        if (pointState is Unknown) {
            if (Point(x, y).isFree(this)) Outside else pointState
        } else pointState
    }

fun LoopMap.fillPipeOutsideOne(): LoopMap =
    map2DValues { x, y, pointState ->
        if (pointState is Loop) {
            pointState
                .let { if (x == 0 || getNotNullable(x - 1, y) is Outside) it.westOutside() else it }
                .let { if (y == 0 || getNotNullable(x, y - 1) is Outside) it.northOutside() else it }
                .let { if (x == width - 1 || getNotNullable(x + 1, y) is Outside) it.eastOutside() else it }
                .let { if (y == height - 1 || getNotNullable(x, y + 1) is Outside) it.southOutside() else it }
        } else {
            pointState
        }
    }

fun LoopMap.fillLoopInformation(): LoopMap {
    tailrec fun go(acc: LoopMap, count: Int): LoopMap =
        if (acc.allMatch { it !is Loop || it.full } || count == 0) {
            acc
        } else {
            val newMap = map2DValues { x, y, pointState ->
                if (pointState is Loop && !pointState.full) {
                    pointState
                        .let {
                            if (it.isNorthOutside == null) {
                                getNullable(x, y - 1)?.let { northNeighbour ->
                                    if (northNeighbour is Loop) {
                                        pointState.north(northNeighbour.isSouthOutside)
                                    } else pointState
                                } ?: pointState
                            } else pointState
                        }
                        .let {
                            if (it.isSouthOutside == null) {
                                getNullable(x, y + 1)?.let { southNeighbour ->
                                    if (southNeighbour is Loop) {
                                        pointState.south(southNeighbour.isNorthOutside)
                                    } else pointState
                                } ?: pointState
                            } else pointState
                        }
                        .let {
                            if (it.isEastOutside == null) {
                                getNullable(x + 1, y)?.let { eastNeighbour ->
                                    if (eastNeighbour is Loop) {
                                        pointState.east(eastNeighbour.isWestOutside)
                                    } else pointState
                                } ?: pointState
                            } else pointState
                        }
                        .let {
                            if (it.isWestOutside == null) {
                                getNullable(x - 1, y)?.let { westNeighbour ->
                                    if (westNeighbour is Loop) {
                                        pointState.west(westNeighbour.isEastOutside)
                                    } else pointState
                                } ?: pointState
                            } else pointState
                        }
                        .let { newPoint ->
                            if (!newPoint.full) {
                                when (newPoint.pipe) {
                                    Pipe.Companion.NE ->
                                        newPoint
                                            .let {
                                                if (it.isSouthOutside == null) {
                                                    val eastNeighbour = getNotNullable(x + 1, y) as Loop
                                                    newPoint.copy(isSouthOutside = eastNeighbour.isSouthOutside)
                                                } else it
                                            }
                                            .let {
                                                if (it.isWestOutside == null) {
                                                    val northNeighbour = getNotNullable(x, y - 1) as Loop
                                                    newPoint.copy(isWestOutside = northNeighbour.isWestOutside)
                                                } else it
                                            }

                                    Pipe.Companion.NS ->
                                        newPoint
                                            .let {
                                                if (it.isWestOutside == null) {
                                                    val northNeighbour = getNotNullable(x, y - 1) as Loop
                                                    val isWestOutside = northNeighbour.isWestOutside
                                                    val isEastOutside = isWestOutside?.let { !it }
                                                    newPoint.copy(isWestOutside = isWestOutside, isEastOutside = isEastOutside)
                                                } else it
                                            }
                                            .let {
                                                if (it.isWestOutside == null) {
                                                    val southNeighbour = getNotNullable(x, y + 1) as Loop
                                                    val isWestOutside = southNeighbour.isWestOutside
                                                    val isEastOutside = isWestOutside?.let { !it }
                                                    newPoint.copy(isWestOutside = isWestOutside, isEastOutside = isEastOutside)
                                                } else it
                                            }

                                    Pipe.Companion.NW ->
                                        newPoint
                                            .let {
                                                if (it.isSouthOutside == null) {
                                                    val westNeighbour = getNotNullable(x - 1, y) as Loop
                                                    newPoint.copy(isSouthOutside = westNeighbour.isSouthOutside)
                                                } else it
                                            }
                                            .let {
                                                if (it.isEastOutside == null) {
                                                    val northNeighbour = getNotNullable(x, y - 1) as Loop
                                                    newPoint.copy(isEastOutside = northNeighbour.isEastOutside)
                                                } else it
                                            }

                                    Pipe.Companion.SW ->
                                        newPoint
                                            .let {
                                                if (it.isNorthOutside == null) {
                                                    val westNeighbour = getNotNullable(x - 1, y) as Loop
                                                    newPoint.copy(isNorthOutside = westNeighbour.isNorthOutside)
                                                } else it
                                            }
                                            .let {
                                                if (it.isEastOutside == null) {
                                                    val southNeighbour = getNotNullable(x, y + 1) as Loop
                                                    newPoint.copy(isEastOutside = southNeighbour.isEastOutside)
                                                } else it
                                            }

                                    Pipe.Companion.SE ->
                                        newPoint
                                            .let {
                                                if (it.isNorthOutside == null) {
                                                    val eastNeighbour = getNotNullable(x + 1, y) as Loop
                                                    newPoint.copy(isNorthOutside = eastNeighbour.isNorthOutside)
                                                } else it
                                            }
                                            .let {
                                                if (it.isWestOutside == null) {
                                                    val southNeighbour = getNotNullable(x, y + 1) as Loop
                                                    newPoint.copy(isWestOutside = southNeighbour.isWestOutside)
                                                } else it
                                            }

                                    Pipe.Companion.WE ->
                                        newPoint
                                            .let {
                                                if (it.isNorthOutside == null) {
                                                    val eastNeighbour = getNotNullable(x + 1, y) as Loop
                                                    val isNorthOutside = eastNeighbour.isNorthOutside
                                                    val isSouthOutside = isNorthOutside?.let { !it }
                                                    newPoint.copy(
                                                        isNorthOutside = isNorthOutside,
                                                        isSouthOutside = isSouthOutside
                                                    )
                                                } else it
                                            }
                                            .let {
                                                if (it.isNorthOutside == null) {
                                                    val westNeighbour = getNotNullable(x - 1, y) as Loop
                                                    val isNorthOutside = westNeighbour.isNorthOutside
                                                    val isSouthOutside = isNorthOutside?.let { !it }
                                                    newPoint.copy(
                                                        isNorthOutside = isNorthOutside,
                                                        isSouthOutside = isSouthOutside
                                                    )
                                                } else it
                                            }
                            }
                        } else newPoint
                    }
                } else {
                    pointState
                }
            }
            go(newMap, count - 1)
        }
    return go(this, 100)
}

fun List<String>.parsed(startPipe: Pipe): PipesMap =
    mapIndexed { y, next ->
        y to next.parsed(startPipe)
    }.toMap()

fun String.parsed(startPipe: Pipe): Map<Int, Pipe?> =
    mapIndexed { x, c ->
        val pipe = if (c == 'S') startPipe else Pipe.fromChar(c)
        x to pipe
    }.toMap()

data class Point(
    val x: Int,
    val y: Int,
) {
    fun north(): Point = Point(x, y - 1)
    fun south(): Point = Point(x, y + 1)
    fun west(): Point = Point(x - 1, y)
    fun east(): Point = Point(x + 1, y)

    fun isBorder(height: Int, width: Int): Boolean =
        x == 0 || x == width - 1 || y == 0 || y == height - 1

    fun isFree(loop: LoopMap): Boolean =
        isFreeToNorth(loop) || isFreeToSouth(loop) || isFreeToWest(loop) || isFreeToEast(loop)

    private fun isFreeToNorth(loop: LoopMap): Boolean =
        (0 until y).all { loop.get(Point(x, it)) !is Loop }

    private fun isFreeToSouth(loop: LoopMap): Boolean =
        (y + 1 until loop.height).all { loop.get(Point(x, it)) !is Loop }

    private fun isFreeToWest(loop: LoopMap): Boolean =
        (0 until x).all { loop.get(Point(it, y)) !is Loop }

    private fun isFreeToEast(loop: LoopMap): Boolean =
        (x + 1 until loop.width).all { loop.get(Point(it, y)) !is Loop }
}

fun <T> Map<Int, Map<Int, T>>.get(point: Point): T =
    get(point.y)!![point.x]!!

sealed interface PointState

data object Inside : PointState
data object Outside : PointState
data object Unknown : PointState
data class Loop(
    val pipe: Pipe,
    val isNorthOutside: Boolean? = null,
    val isSouthOutside: Boolean? = null,
    val isEastOutside: Boolean? = null,
    val isWestOutside: Boolean? = null,
) : PointState {
    val full: Boolean =
        isNorthOutside != null && isSouthOutside != null && isEastOutside != null && isWestOutside != null

    fun south(outside: Boolean?): Loop =
        when (outside) {
            null -> this
            true -> southOutside()
            false -> southInside()
        }

    fun north(outside: Boolean?): Loop =
        when (outside) {
            null -> this
            true -> northOutside()
            false -> northInside()
        }

    fun east(outside: Boolean?): Loop =
        when (outside) {
            null -> this
            true -> eastOutside()
            false -> eastInside()
        }

    fun west(outside: Boolean?): Loop =
        when (outside) {
            null -> this
            true -> westOutside()
            false -> westInside()
        }

    fun northOutside(): Loop =
        when (pipe) {
            Pipe.Companion.NE -> throw IllegalStateException("North for $pipe can not be outside")
            Pipe.Companion.NS -> throw IllegalStateException("North for $pipe can not be outside")
            Pipe.Companion.NW -> throw IllegalStateException("North for $pipe can not be outside")
            Pipe.Companion.SE -> copy(isNorthOutside = true, isWestOutside = true)
            Pipe.Companion.SW -> copy(isNorthOutside = true, isEastOutside = true)
            Pipe.Companion.WE -> copy(isNorthOutside = true, isSouthOutside = false)
        }

    fun northInside(): Loop =
        when (pipe) {
            Pipe.Companion.NE -> throw IllegalStateException("North for $pipe can not be inside")
            Pipe.Companion.NS -> throw IllegalStateException("North for $pipe can not be inside")
            Pipe.Companion.NW -> throw IllegalStateException("North for $pipe can not be inside")
            Pipe.Companion.SE -> copy(isNorthOutside = false, isWestOutside = false)
            Pipe.Companion.SW -> copy(isNorthOutside = false, isEastOutside = false)
            Pipe.Companion.WE -> copy(isNorthOutside = false, isSouthOutside = true)
        }

    fun southOutside(): Loop =
        when (pipe) {
            Pipe.Companion.SE -> throw IllegalStateException("South for $pipe can not be outside")
            Pipe.Companion.NS -> throw IllegalStateException("South for $pipe can not be outside")
            Pipe.Companion.SW -> throw IllegalStateException("South for $pipe can not be outside")
            Pipe.Companion.NE -> copy(isSouthOutside = true, isWestOutside = true)
            Pipe.Companion.NW -> copy(isSouthOutside = true, isEastOutside = true)
            Pipe.Companion.WE -> copy(isNorthOutside = false, isSouthOutside = true)
        }

    fun southInside(): Loop =
        when (pipe) {
            Pipe.Companion.SE -> throw IllegalStateException("South for $pipe can not be inside")
            Pipe.Companion.NS -> throw IllegalStateException("South for $pipe can not be inside")
            Pipe.Companion.SW -> throw IllegalStateException("South for $pipe can not be inside")
            Pipe.Companion.NE -> copy(isSouthOutside = false, isWestOutside = false)
            Pipe.Companion.NW -> copy(isSouthOutside = false, isEastOutside = false)
            Pipe.Companion.WE -> copy(isNorthOutside = true, isSouthOutside = false)
        }

    fun eastOutside(): Loop =
        when (pipe) {
            Pipe.Companion.SE -> throw IllegalStateException("East for $pipe can not be outside")
            Pipe.Companion.NE -> throw IllegalStateException("East for $pipe can not be outside")
            Pipe.Companion.WE -> throw IllegalStateException("East for $pipe can not be outside")
            Pipe.Companion.NS -> copy(isEastOutside = true, isWestOutside = false)
            Pipe.Companion.NW -> copy(isEastOutside = true, isSouthOutside = true)
            Pipe.Companion.SW -> copy(isEastOutside = true, isNorthOutside = true)
        }

    fun eastInside(): Loop =
        when (pipe) {
            Pipe.Companion.SE -> throw IllegalStateException("East for $pipe can not be inside")
            Pipe.Companion.NE -> throw IllegalStateException("East for $pipe can not be inside")
            Pipe.Companion.WE -> throw IllegalStateException("East for $pipe can not be inside")
            Pipe.Companion.NS -> copy(isEastOutside = false, isWestOutside = true)
            Pipe.Companion.NW -> copy(isEastOutside = false, isSouthOutside = false)
            Pipe.Companion.SW -> copy(isEastOutside = false, isNorthOutside = false)
        }

    fun westOutside(): Loop =
        when (pipe) {
            Pipe.Companion.SW -> throw IllegalStateException("South for $pipe can not be outside")
            Pipe.Companion.NW -> throw IllegalStateException("South for $pipe can not be outside")
            Pipe.Companion.WE -> throw IllegalStateException("South for $pipe can not be outside")
            Pipe.Companion.NE -> copy(isWestOutside = true, isSouthOutside = true)
            Pipe.Companion.NS -> copy(isWestOutside = true, isEastOutside = false)
            Pipe.Companion.SE -> copy(isWestOutside = true, isNorthOutside = true)
        }

    fun westInside(): Loop =
        when (pipe) {
            Pipe.Companion.SW -> throw IllegalStateException("South for $pipe can not be inside")
            Pipe.Companion.NW -> throw IllegalStateException("South for $pipe can not be inside")
            Pipe.Companion.WE -> throw IllegalStateException("South for $pipe can not be inside")
            Pipe.Companion.NE -> copy(isWestOutside = false, isSouthOutside = false)
            Pipe.Companion.NS -> copy(isWestOutside = false, isEastOutside = true)
            Pipe.Companion.SE -> copy(isWestOutside = false, isNorthOutside = false)
        }

    companion object {
        fun fromPipe(pipe: Pipe): Loop =
            when (pipe) {
                Pipe.Companion.NE -> Loop(pipe = pipe, isNorthOutside = false, isEastOutside = false)
                Pipe.Companion.NS -> Loop(pipe = pipe, isNorthOutside = false, isSouthOutside = false)
                Pipe.Companion.NW -> Loop(pipe = pipe, isNorthOutside = false, isWestOutside = false)
                Pipe.Companion.SE -> Loop(pipe = pipe, isSouthOutside = false, isEastOutside = false)
                Pipe.Companion.SW -> Loop(pipe = pipe, isSouthOutside = false, isWestOutside = false)
                Pipe.Companion.WE -> Loop(pipe = pipe, isWestOutside = false, isEastOutside = false)
            }
    }
}

sealed interface Pipe {
    fun next(point: Point): Pair<Point, Point>
    val char: Char

    companion object {
        data object NS : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.north(), point.south())

            override val char = '|'
        }

        data object WE : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.west(), point.east())

            override val char = '-'
        }

        data object NE : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.north(), point.east())

            override val char = 'L'
        }

        data object NW : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.north(), point.west())

            override val char = 'J'
        }

        data object SW : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.south(), point.west())

            override val char = '7'
        }

        data object SE : Pipe {
            override fun next(point: Point): Pair<Point, Point> =
                Pair(point.south(), point.east())

            override val char = 'F'
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

fun dump(pipesMap: PipesMap): String = pipesMap.dump { pipe -> pipe?.char ?: '.' }

@JvmName("booleanDump")
fun dump(loop: LoopMap): String =
    loop.dump { cell ->
        when (cell) {
            is Loop -> cell.pipe.char
            Inside -> 'I'
            Outside -> 'O'
            Unknown -> '?'
        }
    }

