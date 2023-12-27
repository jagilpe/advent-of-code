package com.gilpereda.adventsofcode.adventsofcode2021.day20

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable

class Day20Test : BaseTest() {
    override val example: String = """
        ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

        #..#.
        #....
        ##..#
        ..#..
        ..###
    """.trimIndent()

    override val result1: String = "35"

    override val result2: String = "3351"

    override val input: String = "/day20/input.txt"

    override val run1: Executable = part1

    override val run2: Executable = part2
}