package com.gilpereda.aoc2022.day17

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day17Test : BaseTest() {
    override val example: String = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

    override val result1: String = ""

    override val result2: String
        get() = TODO()

    override val input: String = "/day17/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

}