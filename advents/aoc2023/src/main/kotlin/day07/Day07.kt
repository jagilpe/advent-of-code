package com.gilpereda.aoc2022.day07

/**
 * first attempt: 250560426
 * second attempt: 250560426 too high
 * third attempt: 248018032 too high
 * forth:         248106450
 * fifth:         247899149 too low
 */

fun firstTask(input: Sequence<String>): String {
    val sorted = input.parsed()
        .sorted()
    return sorted
        .mapIndexed { index, game -> (index + 1) * game.bet }.sum().toString()
}

fun secondTask(input: Sequence<String>): String {
    val sorted = input.parsedWithJokers()
        .sorted()
        .checked()
    return sorted
        .mapIndexed { index, game -> (index + 1) * game.bet }.sum().toString()
}

fun Sequence<String>.parsed(): List<Game> =
    map { Game.fromLine(it) }.toList()


fun Sequence<String>.parsedWithJokers(): List<Game> =
    map { Game.fromLineWithJokers(it) }.toList()

fun List<Game>.checked(): List<Game> {
    checkOrder(this)
    checkParsing(this)
    return this
}

fun checkOrder(games: List<Game>) {
    games.windowed(2, 1, false)
        .forEach { (first, second) ->
            val order = first.cards.zip(second.cards)
                .map { (one, other) -> CardWithJoker.valueOf("C$one") to CardWithJoker.valueOf("C$other") }
                .map { (one, other) -> one.compareTo(other) }
                .first { it != 0 }

            if (order > 0) {
                println("wrong order: ${first} should be after ${second}")
            }
        }
}

fun checkParsing(games: List<Game>) {
    games.forEach { game ->
        if (game.wrongParsed) println("Wrong parsed game: $game")
    }
}

sealed class Game : Comparable<Game> {
    abstract val withJokers: Boolean
    val wrongParsed: Boolean
        get() = !correctlyParsed
    abstract val correctlyParsed: Boolean
    override fun compareTo(other: Game): Int =
        when (this) {
            is FiveOfAKind -> if (other is FiveOfAKind) cards.compare(other.cards, withJokers) else 1
            is FourOfAKind -> when (other) {
                is FiveOfAKind -> -1
                is FourOfAKind -> cards.compare(other.cards, withJokers)
                else -> 1
            }

            is FullHouse -> when (other) {
                is FiveOfAKind -> -1
                is FourOfAKind -> -1
                is FullHouse -> cards.compare(other.cards, withJokers)
                else -> 1
            }

            is ThreeOfAKind -> when (other) {
                is FiveOfAKind -> -1
                is FourOfAKind -> -1
                is FullHouse -> -1
                is ThreeOfAKind -> cards.compare(other.cards, withJokers)
                else -> 1
            }

            is TwoPair -> when (other) {
                is FiveOfAKind -> -1
                is FourOfAKind -> -1
                is FullHouse -> -1
                is ThreeOfAKind -> -1
                is TwoPair -> cards.compare(other.cards, withJokers)
                else -> 1
            }

            is OnePair -> when (other) {
                is FiveOfAKind -> -1
                is FourOfAKind -> -1
                is FullHouse -> -1
                is ThreeOfAKind -> -1
                is TwoPair -> -1
                is OnePair -> cards.compare(other.cards, withJokers)
                else -> 1
            }

            is HighCard -> when (other) {
                is FiveOfAKind -> -1
                is FourOfAKind -> -1
                is FullHouse -> -1
                is ThreeOfAKind -> -1
                is TwoPair -> -1
                is OnePair -> -1
                is HighCard -> cards.compare(other.cards, withJokers)
            }
        }

    abstract val bet: Long
    abstract val cards: String

    companion object {
        data class FiveOfAKind(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() =
                    (cards.toSet().size == 1) ||
                            (cards.toSet().size == 2 && cards.contains('J'))

        }

        data class FourOfAKind(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() =
                    (cards.toSet().size == 2 && !cards.contains("J")) ||
                            (cards.toSet().size == 3 && cards.count { it == 'J' } in 1..3)
        }

        data class FullHouse(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() =
                    (cards.toSet().size == 2 && !cards.contains("J")) ||
                        (cards.toSet().size == 3 && cards.count { it == 'J' } == 1)
        }

        data class ThreeOfAKind(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() =
                    (cards.toSet().size == 4 && cards.count { it == 'J' } > 0) ||
                            (cards.toSet().size == 3 && !cards.contains("J"))
        }

        data class TwoPair(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() =
                    (cards.toSet().size == 4 && cards.count { it == 'J' } == 1) ||
                            (cards.toSet().size == 3 && !cards.contains("J"))
        }

        data class OnePair(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() =
                    (cards.toSet().size == 5 && cards.contains("J")) ||
                            (cards.toSet().size == 4 && cards.count { it == 'J' } != 2)
        }

        data class HighCard(
            override val bet: Long,
            override val cards: String,
            override val withJokers: Boolean,
        ) : Game() {
            override val correctlyParsed: Boolean
                get() = cards.toSet().size == 5 && !cards.contains("J")
        }

        fun fromLine(line: String): Game =
            line.split(" ").let { (cards, betStr) ->
                val bet = betStr.toLong()
                val sortedCards = cards.groupBy { it }.mapValues { (_, v) -> v.count() }
                    .map { (k, v) -> k to v }
                    .sortedByDescending { (_, v) -> v }

                when (sortedCards.first().second) {
                    5 -> FiveOfAKind(bet = bet, cards = cards, withJokers = false)
                    4 ->
                        FourOfAKind(
                            bet = bet,
                            cards = cards, withJokers = false
                        )

                    3 ->
                        if (sortedCards[1].second == 2)
                            FullHouse(
                                bet = bet,
                                cards = cards, withJokers = false
                            )
                        else ThreeOfAKind(
                            bet,
                            cards = cards, withJokers = false
                        )

                    2 ->
                        if (sortedCards[1].second == 2)
                            TwoPair(
                                bet = bet, cards = cards, withJokers = false
                            )
                        else OnePair(
                            bet = bet, cards = cards, withJokers = false
                        )

                    else -> HighCard(bet = bet, cards = cards, withJokers = false)
                }
            }

        fun fromLineWithJokers(line: String): Game =
            line.split(" ").let { (cards, betStr) ->
                val bet = betStr.toLong()
                val cardsDistribution = cards.groupBy { it }.mapValues { (_, v) -> v.count() }
                val jokers = cardsDistribution.jokers
                val sortedCards = cardsDistribution.map { (k, v) -> k to v }
                    .sortedByDescending { (_, v) -> v }

                when (sortedCards.first().second) {
                    5 -> FiveOfAKind(bet = bet, cards = cards, withJokers = true)
                    4 -> if (jokers > 0) FiveOfAKind(bet, cards, withJokers = true) else FourOfAKind(
                        bet,
                        cards,
                        withJokers = true
                    )

                    3 -> when (jokers) {
                        0 -> if (sortedCards[1].second == 2) FullHouse(bet, cards, withJokers = true) else ThreeOfAKind(
                            bet,
                            cards,
                            withJokers = true
                        )

                        1 -> FourOfAKind(bet, cards, withJokers = true)
                        2 -> FiveOfAKind(bet, cards, withJokers = true)
                        3 -> if (sortedCards[1].second == 2) FiveOfAKind(
                            bet,
                            cards,
                            withJokers = true
                        ) else FourOfAKind(bet, cards, withJokers = true)

                        else -> throw IllegalStateException("impossible state")
                    }

                    2 -> when (jokers) {
                        0 -> if (sortedCards[1].second == 2) TwoPair(bet, cards, withJokers = true) else OnePair(
                            bet,
                            cards,
                            withJokers = true
                        )

                        1 -> if (sortedCards[1].second == 2) FullHouse(bet, cards, withJokers = true) else ThreeOfAKind(
                            bet,
                            cards,
                            withJokers = true
                        )

                        2 -> if (sortedCards[1].second == 2) FourOfAKind(
                            bet,
                            cards,
                            withJokers = true
                        ) else ThreeOfAKind(bet, cards, withJokers = true)

                        else -> throw IllegalStateException("impossible state")
                    }

                    else -> if (jokers == 1) OnePair(bet, cards, true) else HighCard(bet, cards, withJokers = true)
                }
            }
    }
}

val Map<Char, Int>.jokers: Int
    get() = this['J'] ?: 0

val cardsRanking = mapOf(
    '2' to 1,
    '3' to 2,
    '4' to 3,
    '5' to 4,
    '6' to 5,
    '7' to 6,
    '8' to 7,
    '9' to 8,
    'T' to 9,
    'J' to 10,
    'Q' to 11,
    'K' to 12,
    'A' to 13,
)

fun String.compare(other: String, withJokers: Boolean): Int =
    when (val compare1 = first().compare(other.first(), withJokers)) {
        0 -> when (val compare2 = this[1].compare(other[1], withJokers)) {
            0 -> when (val compare3 = this[2].compare(other[2], withJokers)) {
                0 -> when (val compare4 = this[3].compare(other[3], withJokers)) {
                    0 -> this[4].compare(other[4], withJokers)
                    else -> compare4
                }

                else -> compare3
            }

            else -> compare2
        }

        else -> compare1
    }

fun Char.compare(other: Char, withJokers: Boolean): Int =
    if (withJokers) {
        cardsRankingJokers[this]!!.compareTo(cardsRankingJokers[other]!!)
    } else {
        cardsRanking[this]!!.compareTo(cardsRanking[other]!!)
    }

enum class CardWithJoker {
    CJ,
    C2,
    C3,
    C4,
    C5,
    C6,
    C7,
    C8,
    C9,
    CT,
    CQ,
    CK,
    CA,
}

val cardsRankingJokers = mapOf(
    'J' to 1,
    '2' to 2,
    '3' to 3,
    '4' to 4,
    '5' to 5,
    '6' to 6,
    '7' to 7,
    '8' to 8,
    '9' to 9,
    'T' to 10,
    'Q' to 11,
    'K' to 12,
    'A' to 13,
)
