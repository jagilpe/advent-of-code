package com.gilpereda.adventsofcode.adventsofcode2021.day11

fun octopus(input: Sequence<String>): String =
    generateSequence(input.parsed, ::nextStep)
        .map(Octopuses::flashes)
        .take(101).sum().toString()

fun synchonized(input: Sequence<String>): String =
    generateSequence(input.parsed, ::nextStep)
        .mapIndexed(::Pair)
        .first{ it.second.isSynchronized() }
        .first.toString()


typealias Octopuses = List<List<Int>>
data class Point(
    val x: Int,
    val y: Int
) {
    val neighbours: List<Point>
        get() = listOf(
            Point(x - 1, y - 1),
            Point(x , y - 1),
            Point(x + 1, y - 1),
            Point(x + 1, y),
            Point(x + 1, y + 1),
            Point(x, y + 1),
            Point(x - 1, y + 1),
            Point(x - 1, y),
        )
}

fun Octopuses.get(point: Point): Int {
    if (point.y in indices) {
        val row = this[point.y]
        if (point.x in row.indices) {
            return row[point.x]
        }
    }
    return 0
}

fun Octopuses.isSynchronized(): Boolean = flatten().all { it == 0 }

fun Octopuses.octopusHasFlashed(point: Point): Boolean =
    get(point) == 10

val Sequence<String>.parsed: Octopuses
    get() = map { it.toList().map { "$it".toInt() } }.toList()


fun nextStep(octopuses: Octopuses): Octopuses {
    val init = initStep(octopuses)
    val updated = init.updateFlash()
    return generateSequence(updated) { val updateFlash = it.updateFlash()
        updateFlash
    }
        .filter { !hasFlashed(it) }
        .firstOrNull()?.resetFlashes() ?: init
}


fun initStep(octopuses: Octopuses): Octopuses = octopuses.map { row -> row.map { it + 1 } }

fun Octopuses.updateFlash(): Octopuses =
    mapIndexed { y, row ->
        row.mapIndexed { x, octopus -> Point(x, y).next(octopus, this)}
    }

fun Octopuses.resetFlashes(): Octopuses {
    val reset = map { row -> row.map { if (it > 9) 0 else it } }
    return reset
}

fun hasFlashed(octopuses: Octopuses): Boolean = octopuses.any { row -> row.any { it == 10 } }

val Octopuses.flashes: Int
    get() = flatten().count { it == 0 }

fun Point.next(current: Int, octopuses: Octopuses): Int =
    when  {
        current >= 10 -> current + 1
        else -> minOf(current + neighbours.count{ octopuses.octopusHasFlashed(it)}, 10)
    }

fun Octopuses.print(header: String) {
    val out = """
$header
${asString}
        
    """.trimIndent()
    println(out)
}

val Octopuses.asString: String
    get() = joinToString("\n") { row -> row.joinToString(separator = " ", transform = { if (it > 9) "$it" else " $it"}) }