package com.gilpereda.aoc2022.day11

fun firstTask(input: Sequence<String>): String =
    runTask(input, 20, 3)

fun secondTask(input: Sequence<String>): String =
    runTask(input, 10_000, 1)

fun runTask(input: Sequence<String>, times: Int, divideWorryLevelBy: Long): String {
    val text = input.toList()
    val monkeys = text.joinToString("\n").parsed()
    val mcm = monkeys.map { it.divisibleBy }.reduce { one, other -> one * other }
    val initialState = State(
        monkeys = monkeys,
        inspections = List(monkeys.size) { index -> index to 0L }.toMap()
    )
    return (0 until times).fold(initialState) { state, round ->
        round(state, divideWorryLevelBy, mcm).also {
            if (round in setOf(1, 20, 1_000, 2_000, 3_000)) {
                println("Round: $round")
                println("Inspections: ${state.inspections}")
            }
        }
    }.inspections
        .map { (_, inspections) -> inspections }
        .sortedDescending().take(2).reduce { acc, next -> acc * next }.toString()
}

fun round(state: State, divideWorryLevelBy: Long, mcm: Long): State =
    state.monkeys.foldIndexed(state) { id, state, _ -> turn(id, state, divideWorryLevelBy, mcm) }

fun turn(id: Int, state: State, divideWorryLevelBy: Long, mcm: Long): State {
    val monkey = state.monkeys[id]!!
    val inspected = monkey.items.size
    val throwTo = monkey.items.map { item ->
        if (item < 0) throw Exception("Illegal item: $item")
        val worryLevel: Long = monkey.operation.apply(item) / divideWorryLevelBy
        (if (worryLevel % monkey.divisibleBy == 0L) monkey.ifTrueThrowToMonkey else monkey.ifFalseThrowToMonkey) to worryLevel % mcm
    }.groupBy({ it.first }, { it.second })

    return State(
        monkeys = state.monkeys.mapIndexed { i, m ->
            when (i) {
                id -> monkey.copy(items = listOf())
                in throwTo.keys -> {
                    m.copy(items = m.items + throwTo[i]!!)
                }

                else -> {
                    m
                }
            }
        },
        inspections = state.inspections + mapOf(id to state.inspections[id]!! + inspected)
    )
}

data class State(
    val monkeys: List<Monkey>,
    val inspections: Map<Int, Long>,
)

fun String.parsed(): List<Monkey> =
    split("\n\n").map {
        parseMonkey(it.split("\n"))
    }

fun parseMonkey(lines: List<String>): Monkey {
    val startingItems = lines[1].removePrefix("  Starting items: ").split(", ").map { it.toLong() }
    val operation = lines[2].removePrefix("  Operation: new = old ")
        .let {
            when {
                it == "* old" -> Square
                it.startsWith("*") -> Multiply(it.removePrefix("* ").toInt())
                it.startsWith("+") -> Sum(it.removePrefix("+ ").toInt())
                else -> throw Exception("Unknown operation")
            }
        }
    val divisibleBy = lines[3].removePrefix("  Test: divisible by ").toLong()
    val ifTrueThrowToMonkey = lines[4].removePrefix("    If true: throw to monkey ").toInt()
    val ifFalseThrowToMonkey = lines[5].removePrefix("    If false: throw to monkey ").toInt()

    return Monkey(
        items = startingItems,
        operation = operation,
        divisibleBy = divisibleBy,
        ifTrueThrowToMonkey = ifTrueThrowToMonkey,
        ifFalseThrowToMonkey = ifFalseThrowToMonkey,
    )
}

data class Monkey(
    val items: List<Long>,
    val operation: Operation,
    val divisibleBy: Long,
    val ifTrueThrowToMonkey: Int,
    val ifFalseThrowToMonkey: Int,
)

fun interface Operation {
    fun apply(value: Long): Long
}

data class Sum(
    val other: Int
) : Operation {
    override fun apply(value: Long): Long = other + value
}

data class Multiply(
    val other: Int
) : Operation {
    override fun apply(value: Long): Long = other * value
}

object Square : Operation {
    override fun apply(value: Long): Long = value * value
}