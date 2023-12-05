package com.gilpereda.aoc2022.day05

val SEEDS_REGEX = "seeds:( \\d+ \\d+)+".toRegex()

val RANGE_REGEX = "(\\d+) (\\d+) (\\d+)".toRegex()

fun firstTask(input: Sequence<String>): String =
    input.toList().parsed(::findSeeds)
        .findLowest().toString()

fun secondTask(input: Sequence<String>): String =
    input.toList().parsed(::findSeeds2)
        .findLowest().toString()

fun List<String>.parsed(seeds: (String) -> Sequence<Long>): Almanac {
    val seedsList = seeds(first())
    tailrec fun go(rest: List<String>, almanac: Almanac, current: Transformation? = null): Almanac =
        if (rest.isEmpty()) {
            almanac.withTransformation(current)
        } else {
            val nextLine = rest.first()
            val newRest = rest.drop(1)
            if (nextLine.isBlank()) {
                if (current != null && current.ranges.isEmpty())
                    go(newRest, almanac, current)
                else
                    go(newRest, almanac.withTransformation(current))
            } else {
                go(newRest, almanac, Transformation.next(current, nextLine))
            }
        }

    return go(drop(2), almanac = Almanac(seedsList))
}

fun findSeeds(line: String): Sequence<Long> =
    line.split(" ").mapNotNull { it.toLongOrNull() }.asSequence()

fun findSeeds2(line: String): Sequence<Long> {
    return findSeeds(line).chunked(2).asSequence()
        .flatMap { (initial, length) -> (initial until (initial + length)).asSequence() }
}


data class Almanac(
    val seeds: Sequence<Long> = emptySequence(),
    val transformations: List<Transformation> = emptyList(),
) {
    fun withTransformation(transformation: Transformation?): Almanac =
        copy(transformations = transformation?.let { transformations + it } ?: transformations)

    fun findLowest(): Long =
        seeds.fold(Long.MAX_VALUE) { acc, next ->
            val nextTransform = transformations.fold(next) { acc, transformation ->
                transformation.next(acc)
            }
            if (acc <= nextTransform) acc else nextTransform
        }
}

data class Transformation(
    val name: String,
    val ranges: List<Range> = emptyList(),
) {
    fun next(values: Long): Long =
        ranges.firstNotNullOfOrNull { it.next(values) } ?: values

    companion object {
        fun next(transformation: Transformation?, line: String): Transformation =
            transformation?.copy(ranges = transformation.ranges + parseRange(line)) ?: Transformation(name = line)
    }
}

fun parseRange(line: String): Range =
    RANGE_REGEX.find(line)?.destructured?.let { (destinationStart, sourceStart, length) ->
        Range(destinationStart.toLong(), sourceStart.toLong(), length.toLong())
    }!!

data class Range(
    val destinationStart: Long,
    val sourceStart: Long,
    val length: Long,
) {
    fun next(value: Long): Long? {
        val position = value - sourceStart
        return if (position in (0 until length)) {
            destinationStart + position
        } else {
            null
        }
    }
}