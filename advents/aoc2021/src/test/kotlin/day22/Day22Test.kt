package com.gilpereda.adventsofcode.adventsofcode2021.day22

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
        val expected =
            ReactorCuboid(Cuboid(xRange = -54112..-39298, yRange = -85059..-49293, zRange = -27449..7877), on = true)
        assertThat(parseLine("on x=-54112..-39298,y=-85059..-49293,z=-27449..7877")).isEqualTo(expected)
    }

    @Test
    fun `should parse an off line`() {
        val expected =
            ReactorCuboid(Cuboid(xRange = -5412..-298, yRange = -85059..-49293, zRange = -27449..7877), on = false)
        assertThat(parseLine("off x=-5412..-298,y=-85059..-49293,z=-27449..7877")).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("unions")
    fun `should find the union of two cuboids`(one: Cuboid, other: Cuboid, result: Set<Cuboid>) {
        assertThat(one + other).isEqualTo(result)
    }

    @ParameterizedTest
    @MethodSource("intersections")
    fun `should find the intersection of two cuboids`(one: Cuboid, other: Cuboid, intersection: Cuboid?) {
        assertThat(one intersectionWith other).isEqualTo(intersection)
        assertThat(other intersectionWith one).isEqualTo(intersection)
        assertThat(one intersectionWith one).isEqualTo(one)
    }

    @ParameterizedTest
    @MethodSource("subtractions")
    fun `should find the subtract of two cuboids`(m: String, one: Cuboid, other: Cuboid, supplier: () -> Set<Cuboid>) {
        val result = supplier()
        assertThat(one - other).containsExactlyInAnyOrderElementsOf(result)
    }

    companion object {

        @JvmStatic
        fun unions(): Stream<Arguments> = Stream.of(
            of(
                Cuboid(0..5, 0..5, 0..5),
                Cuboid(6..10, 6..10, 6..10),
                setOf(
                    Cuboid(0..5, 0..5, 0..5),
                    Cuboid(6..10, 6..10, 6..10)
                )
            ),
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-5..5, -5..5, -5..5),
                setOf(
                    Cuboid(-10..10, -10..10, -10..10),
                )
            ),
            of(
                Cuboid(-5..5, -5..5, -5..5),
                Cuboid(-10..10, -10..10, -10..10),
                setOf(
                    Cuboid(-10..10, -10..10, -10..10),
                )
            ),
        )

        @JvmStatic
        fun intersections(): Stream<Arguments> = Stream.of(
            // cuboids that do not intersect
            of(
                Cuboid(0..5, 0..5, 0..5),
                Cuboid(6..10, 6..10, 6..10),
                null,
            ),
            // one cuboid inside the other
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-5..5, -5..5, -5..5),
                Cuboid(-5..5, -5..5, -5..5),
            ),
            /*** Cuboids around the vertices ***/
            // Around (-10, -10, -10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, -15..-5, -15..-5),
                Cuboid(-10..-5, -10..-5, -10..-5),
            ),
            // Around (10, -10, -10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, -15..-5, -15..-5),
                Cuboid(5..10, -10..-5, -10..-5),
            ),
            // Around (-10, 10, -10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, 5..15, -15..-5),
                Cuboid(-10..-5, 5..10, -10..-5),
            ),
            // Around (-10, -10, 10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, -15..-5, 5..15),
                Cuboid(-10..-5, -10..-5, 5..10),
            ),
            // Around (10, 10, -10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, 5..15, -15..-5),
                Cuboid(5..10, 5..10, -10..-5),
            ),
            // Around (10, -10, 10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, -15..-5, 5..15),
                Cuboid(5..10, -10..-5, 5..10),
            ),
            // Around (10, 10, 10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, 5..15, 5..15),
                Cuboid(5..10, 5..10, 5..10),
            ),
            // Around (-10, 10, 10)
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, 5..15, 5..15),
                Cuboid(-10..-5, 5..10, 5..10),
            ),
            // Cuboids around the cube faces
            // Around face in x = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..15, -15..15),
                Cuboid(0..10, -10..10, -10..10),
            ),
            // Around face in x = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..15, -15..15),
                Cuboid(-10..0, -10..10, -10..10),
            ),
            // Around face in y = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, 0..15, -15..15),
                Cuboid(-10..10, 0..10, -10..10),
            ),
            // Around face in y = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..0, -15..15),
                Cuboid(-10..10, -10..0, -10..10),
            ),
            // Around face in z = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..15, 0..15),
                Cuboid(-10..10, -10..10, 0..10),
            ),
            // Around face in z = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..15, -15..0),
                Cuboid(-10..10, -10..10, -10..0),
            ),
            // Cuboids around the edges
            // edge in x = 10, y = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, 0..15, -15..15),
                Cuboid(0..10, 0..10, -10..10),
            ),
            // edge in x = 10, y = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..0, -15..15),
                Cuboid(0..10, -10..0, -10..10),
            ),
            // edge in x = -10, y = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, 0..15, -15..15),
                Cuboid(-10..0, 0..10, -10..10),
            ),
            // edge in x = -10, y = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..0, -15..15),
                Cuboid(-10..0, -10..0, -10..10),
            ),
            // edge in x = 10, z = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..15, 0..15),
                Cuboid(0..10, -10..10, 0..10),
            ),
            // edge in x = 10, z = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..15, -15..0),
                Cuboid(0..10, -10..10, -10..0),
            ),
            // edge in x = -10, z = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..15, 0..15),
                Cuboid(-10..0, -10..10, 0..10),
            ),
            // edge in x = -10, z = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..15, -15..0),
                Cuboid(-10..0, -10..10, -10..0),
            ),
            // edge in y = 10, z = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, 0..15, 0..15),
                Cuboid(-10..10, 0..10, 0..10),
            ),
            // edge in y = 10, z = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, 0..15, -15..0),
                Cuboid(-10..10, 0..10, -10..0),
            ),
            // edge in y = -10, z = 10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..0, 0..15),
                Cuboid(-10..10, -10..0, 0..10),
            ),
            // edge in y = -10, z = -10
            of(
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..0, -15..0),
                Cuboid(-10..10, -10..0, -10..0),
            ),
        )

        @JvmStatic
        fun subtractions(): Stream<Arguments> = Stream.of(
            of(
                "Cuboids that do not intersect",
                Cuboid(0..5, 0..5, 0..5),
                Cuboid(6..10, 6..10, 6..10),
                { setOf(Cuboid(0..5, 0..5, 0..5)) },
            ),
            of(
                "subtrahend inside the minuend",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-5..5, -5..5, -5..5),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..-6),
                        Cuboid(-10..10, -10..10, 6..10),
                        Cuboid(-10..-6, -10..10, -5..5),
                        Cuboid(6..10, -10..10, -5..5),
                        Cuboid(-5..5, -10..-6, -5..5),
                        Cuboid(-5..5, 6..10, -5..5),
                    )
                },
            ),
            of(
                "minuend inside the subtrahend",
                Cuboid(-5..5, -5..5, -5..5),
                Cuboid(-10..10, -10..10, -10..10),
                { emptySet<Cuboid>() },
            ),
            of(
                "Around (-10, -10, -10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, -15..-5, -15..-5),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -4..10),
                        Cuboid(-4..10, -10..10, -10..-5),
                        Cuboid(-10..-5, -4..10, -10..-5),
                    )
                },
            ),
            of(
                "Around (10, -10, -10)  vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, -15..-5, -15..-5),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -4..10),
                        Cuboid(-10..4, -10..10, -10..-5),
                        Cuboid(5..10, -4..10, -10..-5),
                    )
                },
            ),
            of(
                "Around (-10, 10, -10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, 5..15, -15..-5),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -4..10),
                        Cuboid(-4..10, -10..10, -10..-5),
                        Cuboid(-10..-5, -10..4, -10..-5),
                    )
                },
            ),
            of(
                "Around (-10, -10, 10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, -15..-5, 5..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..4),
                        Cuboid(-4..10, -10..10, 5..10),
                        Cuboid(-10..-5, -4..10, 5..10),
                    )
                },
            ),
            of(
                "Around (10, 10, -10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, 5..15, -15..-5),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -4..10),
                        Cuboid(-10..4, -10..10, -10..-5),
                        Cuboid(5..10, -10..4, -10..-5),
                    )
                },
            ),
            of(
                "Around (10, -10, 10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, -15..-5, 5..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..4),
                        Cuboid(-10..4, -10..10, 5..10),
                        Cuboid(5..10, -4..10, 5..10),
                    )
                },
            ),
            of(
                "Around (10, 10, 10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(5..15, 5..15, 5..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..4),
                        Cuboid(-10..4, -10..10, 5..10),
                        Cuboid(5..10, -10..4, 5..10),
                    )
                }
            ),
            of(
                "Around (-10, 10, 10) vertex",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..-5, 5..15, 5..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..4),
                        Cuboid(-4..10, -10..10, 5..10),
                        Cuboid(-10..-5, -10..4, 5..10),
                    )
                },
            ),
            of(
                "Around face in x = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..15, -15..15),
                { setOf(Cuboid(-10..-1, -10..10, -10..10)) }
            ),
            of(
                "Around face in x = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..15, -15..15),
                { setOf(Cuboid(1..10, -10..10, -10..10)) }
            ),
            of(
                "Around face in y = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, 0..15, -15..15),
                { setOf(Cuboid(-10..10, -10..-1, -10..10)) }
            ),
            of(
                "Around face in y = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..0, -15..15),
                { setOf(Cuboid(-10..10, 1..10, -10..10)) }
            ),
            of(
                "Around face in z = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..15, 0..15),
                { setOf(Cuboid(-10..10, -10..10, -10..-1)) }
            ),
            of(
                "Around face in z = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..15, -15..0),
                { setOf(Cuboid(-10..10, -10..10, 1..10)) }
            ),
            of(
                "Around edge in x = 10, y = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, 0..15, -15..15),
                {
                    setOf(
                        Cuboid(-10..-1, -10..10, -10..10),
                        Cuboid(0..10, -10..-1, -10..10),
                    )
                },
            ),
            of(
                "Around edge in x = 10, y = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..0, -15..15),
                {
                    setOf(
                        Cuboid(-10..-1, -10..10, -10..10),
                        Cuboid(0..10, 1..10, -10..10),
                    )
                },
            ),
            of(
                "Around edge in x = -10, y = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, 0..15, -15..15),
                {
                    setOf(
                        Cuboid(1..10, -10..10, -10..10),
                        Cuboid(-10..0, -10..-1, -10..10),
                    )
                },
            ),
            of(
                "Around edge in x = -10, y = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..0, -15..15),
                {
                    setOf(
                        Cuboid(1..10, -10..10, -10..10),
                        Cuboid(-10..0, 1..10, -10..10),
                    )
                },
            ),
            of(
                "Around edge in x = 10, z = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..15, 0..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..-1),
                        Cuboid(-10..-1, -10..10, 0..10),
                    )
                },
            ),
            of(
                "Around edge in x = 10, z = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(0..15, -15..15, -15..0),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, 1..10),
                        Cuboid(-10..-1, -10..10, -10..0),
                    )
                },
            ),
            of(
                "Around edge in x = -10, z = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..15, 0..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..-1),
                        Cuboid(1..10, -10..10, 0..10),
                    )
                },
            ),
            of(
                "Around edge in x = -10, z = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..0, -15..15, -15..0),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, 1..10),
                        Cuboid(1..10, -10..10, -10..0),
                    )
                },
            ),
            of(
                "Around edge in y = 10, z = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, 0..15, 0..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..-1),
                        Cuboid(-10..10, -10..-1, 0..10),
                    )
                },
            ),
            of(
                "Around edge in y = 10, z = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, 0..15, -15..0),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, 1..10),
                        Cuboid(-10..10, -10..-1, -10..0),
                    )
                },
            ),
            of(
                "Around edge in y = -10, z = 10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..0, 0..15),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, -10..-1),
                        Cuboid(-10..10, 1..10, 0..10),
                    )
                },
            ),
            of(
                "Around edge in y = -10, z = -10",
                Cuboid(-10..10, -10..10, -10..10),
                Cuboid(-15..15, -15..0, -15..0),
                {
                    setOf(
                        Cuboid(-10..10, -10..10, 1..10),
                        Cuboid(-10..10, 1..10, -10..0),
                    )
                },
            ),
        )

    }
}