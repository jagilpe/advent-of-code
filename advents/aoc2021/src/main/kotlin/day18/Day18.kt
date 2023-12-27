package com.gilpereda.adventsofcode.adventsofcode2021.day18

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gilpereda.adventsofcode.adventsofcode2021.Executable


val part1: Executable = { input ->
    parseInput(input)
        .reduce { one, two -> (one + two).reduced() }.magnitude.toString()
}

val part2: Executable = { input ->
    val fishes = parseInput(input).toList()
    fishes.asSequence().flatMap { one -> fishes.asSequence().map { other -> Pair(one, other) } }
        .filter { (one, other) -> one != other }
        .flatMap { (one, other) -> sequenceOf((one + other).reduced().magnitude, (other + one).reduced().magnitude) }
        .maxOrNull().toString()
}


fun parseInput(input: Sequence<String>): Sequence<Snailfish> =
    input.map {
        val (snailfish, rest) = parseSnailfish(it)
        if (rest.isEmpty()) {
            snailfish
        } else {
            throw Exception("Parse error. Unexpected rest: $rest")
        }
    }


fun parseSnailfish(line: String): Pair<Snailfish, String> =
    if (line.startsWith("[")) {
        val (first, firstRest) = parseElement(line.drop(1))
        if (firstRest.startsWith(",")) {
            val (second, secondRest) = parseElement(firstRest.drop(1))
            if (secondRest.startsWith("]")) {
                Pair(Snailfish(first, second), secondRest.drop(1))
            } else {
                throw Exception("Error while parsing line $line. It should start with ]")
            }
        } else {
            throw Exception("Error while parsing line $line. It should start with ,")
        }
    } else {
        throw Exception("Error while parsing line $line. It should start with [")
    }

fun parseElement(string: String): Pair<Element, String> {
    return if (string.startsWith("[")) {
        val (snailfish, rest) = parseSnailfish(string)
        Pair(snailfish.right(), rest)
    } else {
        val splitAt = listOf(string.indexOf(','), string.indexOf(']')).filter { it != -1 }.minOrNull()!!
        val element = string.substring(0 until splitAt)
        val rest = string.substring(splitAt)
        Pair(element.toInt().left(), rest)
    }
}

typealias Element = Either<Int, Snailfish>

val Element.asString: String
    get() = fold({ "$it" }, { "$it" })

data class Snailfish(val left: Element, val right: Element) {
    operator fun plus(other: Snailfish): Snailfish = snf(this, other)

    fun reduced(): Snailfish =
        generateSequence(this) { fish ->
            if (fish.needToExplode) {
                fish.exploded
            } else if (fish.needToSplit) {
                fish.splitted
            } else {
                fish
            }
        }.first { it.isReduced }

    val magnitude: Int
        get() = this.right().magnitude

    val isReduced: Boolean
        get() = !needToExplode && !needToSplit

    private val needToExplode: Boolean
        get() = depth() > 4

    private val needToSplit: Boolean
        get() = left.fold(
            { l ->
                right.fold(
                    { r -> l > 9 || r > 9 },
                    { r -> l > 9 || r.needToSplit }
                )
            },
            { l ->
                right.fold(
                    { r -> r > 9 || l.needToSplit },
                    { r -> l.needToSplit || r.needToSplit },
                )
            }
        )

    private fun depth(): Int =
        left.fold(
            {
                right.fold({ 1 }, { it.depth() + 1 })
            },
            { l ->
                right.fold(
                    { l.depth() + 1 },
                    { r ->
                        maxOf(l.depth() + 1, r.depth() + 1)
                    })
            }
        )

    private val exploded: Snailfish
        get() = this.right().explodedElement(4).first
            .let { it.fold({ throw Exception("Impossible state") }, { it }) }

    private val splitted: Snailfish
        get() = this.splitted() ?: this

    override fun toString(): String {
        return "[${left.asString}, ${right.asString}]"
    }

    companion object {
        fun snf(first: Int, second: Int): Snailfish = Snailfish(first.left(), second.left())
        fun snf(first: Int, second: Snailfish): Snailfish = Snailfish(first.left(), second.right())
        fun snf(first: Snailfish, second: Int): Snailfish = Snailfish(first.right(), second.left())
        fun snf(first: Snailfish, second: Snailfish): Snailfish = Snailfish(first.right(), second.right())
    }
}

private fun Element.splitted(): Element? =
    fold(
        { l -> if (l > 9) Snailfish((l/2).left(), (l/2 + l % 2).left() ).right() else null },
        { r -> r.splitted()?.right() }
    )

private fun Snailfish.splitted(): Snailfish? {
    val newLeft = left.splitted()
    return if (newLeft != null) {
        Snailfish(newLeft, right)
    } else {
        val newRight = right.splitted()
        if (newRight != null) {
            Snailfish(left, newRight)
        } else {
            null
        }
    }
}


private fun Element.explodedElement(depth: Int): Pair<Element, Pair<Int?, Int?>?> {
    return if (depth > 0) {
        fold(
            { Pair(this, null) },
            { fish ->
                val (newLeft, leftExploding) = fish.left.explodedElement(depth = depth - 1)
                if (leftExploding != null) {
                    val explodedRight = leftExploding.second
                    val newRight = if (explodedRight != null) fish.right.updateRight(explodedRight) else fish.right
                    Pair(Snailfish(newLeft, newRight).right(), leftExploding.copy(second = null))
                } else {
                    val (newRight, rightExploding) = fish.right.explodedElement(depth = depth - 1)
                    if (rightExploding != null) {
                        val explodedLeft = rightExploding.first
                        val newLeft = if (explodedLeft != null) fish.left.updateLeft(explodedLeft) else fish.left
                        Pair(Snailfish(newLeft, newRight).right(), rightExploding.copy(first = null))
                    } else {
                        Pair(this, null)
                    }
                }
            }
        )
    } else {
        fold(
            { Pair(this, null) },
            { fish ->
                fish.left.fold(
                    { l ->
                        fish.right.fold(
                            { r -> Pair(0.left(), Pair(l, r)) },
                            { throw IllegalStateException() }
                        )
                    },
                    {
                        throw IllegalStateException()
                    }
                )
            }
        )
    }
}

private fun Element.updateRight(value: Int): Element =
    fold(
        { (value + it).left() },
        { Snailfish(it.left.updateRight(value), it.right).right() }
    )

private fun Element.updateLeft(value: Int): Element =
    fold(
        { (value + it).left() },
        { Snailfish(it.left, it.right.updateLeft(value)).right() }
    )

private val Element.magnitude: Int
    get() = fold(
        { it },
        { fish -> 3 * fish.left.magnitude + 2 * fish.right.magnitude }
    )
