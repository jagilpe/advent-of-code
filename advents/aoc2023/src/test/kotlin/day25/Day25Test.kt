package com.gilpereda.aoc2022.day25

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day25Test : BaseTest() {
    override val example: String = """
        jqt: rhn xhk nvd
        rsh: frs pzl lsr
        xhk: hfx
        cmg: qnr nvd lhk bvb
        rhn: xhk bvb hfx
        bvb: xhk hfx
        pzl: lsr hfx nvd
        qnr: nvd
        ntq: jqt hfx bvb xhk
        nvd: lhk
        lsr: lhk
        rzs: qnr cmg lsr rsh
        frs: qnr lhk lsr
    """.trimIndent()

    override val resultExample1: String = "54"

    override val resultExample2: String
        get() = TODO()

    override val resultReal1: String = ""

    override val resultReal2: String = ""

    override val input: String = "/day25/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}