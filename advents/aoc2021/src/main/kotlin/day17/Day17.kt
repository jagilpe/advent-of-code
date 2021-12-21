package com.gilpereda.adventsofcode.adventsofcode2021.day17

data class Path(val xVelocity: Int, val yVelocity: Int) {
    val path: Sequence<Probe> =
        generateSequence(Probe(0, 0, xVelocity, yVelocity)) { (x, y, velX, velY) ->
            val newX = x + velX
            val newY = y + velY
            val newXVel = when {
                velX < 0 -> velX + 1
                velX > 0 -> velX - 1
                else -> 0
            }
            val newYVel = velY - 1
            Probe(newX, newY, newXVel, newYVel)
        }
}

fun part1(target: TargetZone): Int =
    Path(0, target.maxYVel).maxY

fun part2(target: TargetZone): Int =
    with (target) {
        potentialVelX.flatMap { velX ->
            (minYVel..maxYVel).asSequence().map { velY -> Path(velX, velY) }
        }
            .filter {
                it.reachesTarget(this)
            }.count()
    }



val TargetZone.maxYVel: Int
    get() = -yRange.first - 1

val TargetZone.minYVel: Int
    get() = yRange.first



fun Path.reachesTarget(target: TargetZone): Boolean =
    path.takeWhile { (x, y, _, _) -> x <= target.xRange.last && y >= target.yRange.first }
        .any { (x, y, _, _) -> x in target.xRange && y in target.yRange }

val Path.maxY: Int
    get() =
        path.windowed(2).takeWhile { (one, two) -> one.y <= two.y }
            .lastOrNull()?.get(1)?.y ?: path.first().y

val TargetZone.potentialVelX: Sequence<Int>
    get() = (minVelX..maxVelX).asSequence()

val Path.maxX: Int
    get() = path.first { it.velocityX == 0 }.x

val TargetZone.maxVelX: Int
    get() = xRange.last

val TargetZone.minVelX: Int
    get() = generateSequence(2) { i -> i + 1 }
    .takeWhile { (it - 1) * it / 2 < xRange.first }
    .last()

data class TargetZone(val xRange: IntRange, val yRange: IntRange)

data class Probe(val x: Int, val y: Int, val velocityX: Int, val velocityY: Int)
