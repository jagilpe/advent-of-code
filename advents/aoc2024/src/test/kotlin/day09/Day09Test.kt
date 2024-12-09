package com.gilpereda.aoc2024.day09

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day09Test : BaseTest() {
    override val example: String = "2333133121414131402"

    override val resultExample1: String = "1928"

    override val resultReal1: String = "6519155389266"

    override val resultExample2: String = "2858"

    override val resultReal2: String = "6547228115826"

    override val input: String = "/day09/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
