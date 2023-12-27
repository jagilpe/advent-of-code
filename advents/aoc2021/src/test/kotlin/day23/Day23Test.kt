package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.day23.Amphipod.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day23Test {
    val example: Board = Board(
        hall = List(7) { Free },
        rooms = mapOf(
            AMBER to listOf(Occupied(BRONZE), Occupied(AMBER, false)),
            BRONZE to listOf(Occupied(COPPER), Occupied(DESERT)),
            COPPER to listOf(Occupied(BRONZE), Occupied(COPPER, false)),
            DESERT to listOf(Occupied(DESERT), Occupied(AMBER)),
        )
    )
    val input: Board = Board(
        hall = List(7) { Free },
        rooms = mapOf(
            AMBER to listOf(Occupied(AMBER), Occupied(BRONZE)),
            BRONZE to listOf(Occupied(COPPER), Occupied(DESERT)),
            COPPER to listOf(Occupied(COPPER), Occupied(AMBER)),
            DESERT to listOf(Occupied(DESERT), Occupied(BRONZE)),
        )
    )
    val input2: Board = Board(
        hall = List(7) { Free },
        rooms = mapOf(
            AMBER to listOf(Occupied(AMBER), Occupied(DESERT), Occupied(DESERT), Occupied(BRONZE)),
            BRONZE to listOf(Occupied(COPPER), Occupied(COPPER), Occupied(BRONZE), Occupied(DESERT)),
            COPPER to listOf(Occupied(COPPER), Occupied(BRONZE), Occupied(AMBER), Occupied(AMBER)),
            DESERT to listOf(Occupied(DESERT), Occupied(AMBER), Occupied(COPPER), Occupied(BRONZE)),
        )
    )

    val example2: Board = Board(
        hall = listOf(Free, Occupied(AMBER), Free, Occupied(BRONZE), Free, Free, Free),
        rooms = mapOf(
            AMBER to listOf(Free, Occupied(AMBER, false)),
            BRONZE to listOf(Free, Occupied(DESERT)),
            COPPER to listOf(Free, Occupied(COPPER, false)),
            DESERT to listOf(Occupied(DESERT), Occupied(AMBER)),
        )
    )

    val example3: Board = Board(
        hall = listOf(Free, Occupied(BRONZE), Free, Occupied(COPPER), Occupied(BRONZE), Free, Free),
        rooms = mapOf(
            AMBER to listOf(Free, Occupied(AMBER, false)),
            BRONZE to listOf(Free, Occupied(DESERT)),
            COPPER to listOf(Free, Occupied(COPPER, false)),
            DESERT to listOf(Occupied(DESERT), Occupied(AMBER)),
        )
    )

    val finished: Board = Board(
        hall = listOf(Free, Free, Free, Free, Free, Free, Free),
        rooms = mapOf(
            AMBER to listOf(Occupied(AMBER, false), Occupied(AMBER, false)),
            BRONZE to listOf(Occupied(BRONZE, false), Occupied(BRONZE, false)),
            COPPER to listOf(Occupied(COPPER, false), Occupied(COPPER, false)),
            DESERT to listOf(Occupied(DESERT, false), Occupied(DESERT, false)),
        )
    )

    private val expected1 = 12521

    @Test
    fun `should work with the example`() {
        assertThat(part1(input2)).isEqualTo(expected1)
    }

    @Test
    fun `should work`() {
//        val next = Round.firstRound(example)
//            .next(21).next(7).next(3).next(4).next(3).next(5).next(3).next(1).next(1).next(0).next(0).next(0)?.next()

        assertThat(true).isFalse()
    }

}