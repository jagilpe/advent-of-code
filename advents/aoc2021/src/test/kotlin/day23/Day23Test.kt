package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day23.Amphipod.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day23Test {

    @Test
    fun `should work with the example 1`() {
        val example = listOf(
            mutableListOf(Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall),
            mutableListOf(Wall, Hall(), Hall(), Door, Hall(), Door, Hall(), Door, Hall(), Door, Hall(), Hall(), Wall),
            mutableListOf(Wall, Wall, Wall, Room(BRONZE, AMBER), Wall, Room(COPPER, BRONZE), Wall, Room(BRONZE, COPPER), Wall, Room(DESERT, DESERT), Wall, Wall, Wall),
            mutableListOf(Wall, Wall, Wall, Room(AMBER, AMBER), Wall, Room(DESERT, BRONZE), Wall, Room(COPPER, COPPER), Wall, Room(AMBER, DESERT), Wall, Wall, Wall),
            mutableListOf(Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall),
        )
        assertThat(part1(example)).isEqualTo(12521)
    }

}