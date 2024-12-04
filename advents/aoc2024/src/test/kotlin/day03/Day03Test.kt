package com.gilpereda.aoc2024.day03

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day03Test : BaseTest() {
    override val example: String =
        "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"

    override val example2: String =
        "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"

    override val resultExample1: String = "161"

    override val resultReal1: String = ""

    override val resultExample2: String = "48"

    override val resultReal2: String = ""

    override val input: String = "/day03/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
