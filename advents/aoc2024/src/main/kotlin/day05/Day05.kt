package com.gilpereda.aoc2024.day05

fun firstTask(input: Sequence<String>): String {
    val (rules, updates, _) = input.parsed()
    return updates
        .filter { it.isValid(rules) }
        .sumOf { it.findMiddle() }
        .toString()
}

private fun Sequence<String>.parsed(): Triple<Rules, List<Update>, Boolean> =
    fold(Triple(Rules(), emptyList<Update>(), false)) { (rules, updates, spaceFound), next ->
        if (!spaceFound) {
            if (next.isBlank()) {
                Triple(rules, updates, true)
            } else {
                Triple(rules.addRule(next), updates, spaceFound)
            }
        } else {
            Triple(rules, updates + Update.from(next), spaceFound)
        }
    }

fun secondTask(input: Sequence<String>): String {
    val (rules, updates, _) = input.parsed()
    return updates
        .filter { !it.isValid(rules) }
        .map {
            var corrected = it
            while (!corrected.isValid(rules)) {
                corrected = corrected.corrected(rules)
            }
            corrected
        }.sumOf {
            it.findMiddle()
        }.toString()
}

class Rules(
    val rules: MutableMap<Int, MutableList<Int>> = mutableMapOf(),
) {
    fun addRule(rule: String): Rules =
        apply {
            val (previous, following) = rule.split("|")
            rules.computeIfAbsent(following.toInt()) { mutableListOf() }.add(previous.toInt())
        }
}

class Update(
    val pages: List<Int>,
) {
    fun isValid(rules: Rules): Boolean {
        tailrec fun go(
            valid: Boolean,
            pages: List<Int>,
        ): Boolean =
            if (pages.isEmpty() || !valid) {
                valid
            } else {
                val next = pages.first()
                val rest = pages.drop(1)
                go(next.isValid(rest, rules), pages.drop(1))
            }
        return go(true, pages)
    }

    fun corrected(rules: Rules): Update {
        tailrec fun go(
            acc: List<Int>,
            pages: List<Int>,
        ): Update =
            if (pages.isEmpty()) {
                Update(acc)
            } else {
                val next = pages.first()
                val rest = pages.drop(1)
                val wrongPages = next.wrongPages(rest, rules)
                go(acc + wrongPages + next, rest - wrongPages)
            }
        return go(emptyList(), pages)
    }

    private fun Int.wrongPages(
        rest: List<Int>,
        rules: Rules,
    ): Set<Int> =
        rules.rules[this]?.let { pages ->
            rest.filter { follower -> follower in pages }.toSet()
        } ?: emptySet()

    private fun Int.isValid(
        following: List<Int>,
        rules: Rules,
    ): Boolean =
        rules.rules[this]?.let { pages ->
            following.all { follower -> follower !in pages }
        } ?: true

    fun findMiddle(): Int {
        val middle = if (pages.size % 2 == 0) (pages.size / 2) - 1 else pages.size / 2
        return pages[middle]
    }

    companion object {
        fun from(line: String): Update = Update(line.split(",").map { it.toInt() })
    }
}
