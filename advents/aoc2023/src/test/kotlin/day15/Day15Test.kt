package com.gilpereda.aoc2022.day15

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day15Test : BaseTest() {
    override val example: String = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"

    override val result1: String = "1320"

    override val result2: String = "145"

    override val input: String = "/day15/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `the focusing power should be correct`() {
        val boxes = BoxMap()
            .add(0, Lens("rn", 1))
            .add(0, Lens("cm", 2))
            .add(3, Lens("ot", 7))
            .add(3, Lens("ab", 5))
            .add(3, Lens("pc", 6))
        assertThat(boxes.focusingPower()).isEqualTo(145)
    }
}