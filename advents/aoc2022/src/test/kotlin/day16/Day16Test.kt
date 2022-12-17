package com.gilpereda.aoc2022.day16

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class Day16Test : BaseTest() {
    override val example: String = """Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II"""

    override val result1: String = "1651"

    override val result2: String = "1707"

    override val input: String = "/day16/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the input`() {
        val valves = example.splitToSequence("\n").parsed().toList()
        val expected = listOf(
            Valve("AA", 0, setOf("DD", "II", "BB")),
            Valve("BB", 13, setOf("CC", "AA")),
            Valve("CC", 2, setOf("DD", "BB")),
            Valve("DD", 20, setOf("CC", "AA", "EE")),
            Valve("EE", 3, setOf("FF", "DD")),
            Valve("FF", 0, setOf("EE", "GG")),
            Valve("GG", 0, setOf("FF", "HH")),
            Valve("HH", 22, setOf("GG")),
            Valve("II", 0, setOf("AA", "JJ")),
            Valve("JJ", 21, setOf("II")),
        )

        assertThat(valves).isEqualTo(expected)
    }

}