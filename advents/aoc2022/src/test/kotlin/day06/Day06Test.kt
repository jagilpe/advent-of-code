package com.gilpereda.aoc2022.day06

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test : BaseTest() {
    override val example: String = "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"

    override val result1: String = "10"

    override val result2: String = "29"

    override val input: String = "/day06/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

}