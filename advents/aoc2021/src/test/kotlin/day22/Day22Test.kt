package com.gilpereda.adventsofcode.adventsofcode2021.day22

import arrow.core.flatten
import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day22Test : BaseTest() {
    override val example: String = """
        on x=-20..26,y=-36..17,z=-47..7
        on x=-20..33,y=-21..23,z=-26..28
        on x=-22..28,y=-29..23,z=-38..16
        on x=-46..7,y=-6..46,z=-50..-1
        on x=-49..1,y=-3..46,z=-24..28
        on x=2..47,y=-22..22,z=-23..27
        on x=-27..23,y=-28..26,z=-21..29
        on x=-39..5,y=-6..47,z=-3..44
        on x=-30..21,y=-8..43,z=-13..34
        on x=-22..26,y=-27..20,z=-29..19
        off x=-48..-32,y=26..41,z=-47..-37
        on x=-12..35,y=6..50,z=-50..-2
        off x=-48..-32,y=-32..-16,z=-15..-5
        on x=-18..26,y=-33..15,z=-7..46
        off x=-40..-22,y=-38..-28,z=23..41
        on x=-16..35,y=-41..10,z=-47..6
        off x=-32..-23,y=11..30,z=-14..3
        on x=-49..-5,y=-3..45,z=-29..18
        off x=18..30,y=-20..-8,z=-3..13
        on x=-41..9,y=-7..43,z=-33..15
        on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
        on x=967..23432,y=45373..81175,z=27513..53682
    """.trimIndent()

    override val result1: String = "590784"

    override val result2: String = "2758514936282235"

    override val input: String = "/day22/input.txt"
    override val run1: Executable = part1
    override val run2: Executable = part2


    @Test
    fun `should parse an on line`() {
        val expected = Cuboid(xRange = -54112..-39298, yRange = -85059..-49293, zRange = -27449..7877, on = true)
        assertThat(parseLine("on x=-54112..-39298,y=-85059..-49293,z=-27449..7877")).isEqualTo(expected)
    }

    @Test
    fun `should parse an off line`() {
        val expected = Cuboid(xRange = -5412..-298, yRange = -85059..-49293, zRange = -27449..7877, on = false)
        assertThat(parseLine("off x=-5412..-298,y=-85059..-49293,z=-27449..7877")).isEqualTo(expected)
    }

    @Test
    fun `should access all the points in the reactor`() {
        val reactor = Reactor(-50..50, -60..60, -70..70)

        assertThatCode {
            (-50..50).asSequence().flatMap { x ->
                (-60..60).asSequence().flatMap { y ->
                    (-70..70).asSequence().map { z ->
                        reactor[Point(x, y, z)]
                    }
                }
            }.all { it }
        }.doesNotThrowAnyException()
    }

    @Test
    fun `should set all the points in the reactor`() {
        val reactor = Reactor(-4..5, -8..12, -10..15)

        (-4..5).map { x ->
            (-8..12).map { y ->
                (-10..15).forEach { z ->
                    reactor.set(Point(x, y, z), true)
                }
            }
        }

        assertThat((-4..5).asSequence().flatMap { x ->
            (-8..12).asSequence().flatMap { y ->
                (-10..15).asSequence().map { z ->
                    reactor[Point(x, y, z)]
                }
            }
        }.all { it }).isTrue
    }

    @ParameterizedTest
    @MethodSource("finalState")
    fun `should get the final state of a point`(
        point: Point, cuboids: List<Cuboid>, finalState: Boolean
    ) {
        assertThat(point.finalState(cuboids)).isEqualTo(finalState)

    }

    companion object {
        @JvmStatic
        fun finalState(): Stream<Arguments> = Stream.of(
            of(Point(10, 10, 10), listOf(cuboidOn(5, 5, 5)), false),
            of(Point(4, 4, 4), listOf(cuboidOn(5, 5, 5)), true),
            of(Point(4, 4, 4), listOf(cuboidOff(5, 5, 5), cuboidOn(5, 5, 5)), false),
            of(Point(8, 4, 4), listOf(cuboidOff(5, 5, 5), cuboidOn(10, 5, 5)), true),
        )
    }
}

private fun cuboidOn(x: Int, y: Int, z: Int): Cuboid =
    Cuboid(0..x, 0..y, 0..z, true)

private fun cuboidOff(x: Int, y: Int, z: Int): Cuboid =
    Cuboid(0..x, 0..y, 0..z, false)