package com.gilpereda.aoc2024.day23

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day23Test : BaseTest() {
    override val example: String =
        """
        kh-tc
        qp-kh
        de-cg
        ka-co
        yn-aq
        qp-ub
        cg-tb
        vc-aq
        tb-ka
        wh-tc
        yn-cg
        kh-ub
        ta-co
        de-co
        tc-td
        tb-wq
        wh-td
        ta-ka
        td-qp
        aq-cg
        wq-ub
        ub-vc
        de-ta
        wq-aq
        wq-vc
        wh-yn
        ka-de
        kh-ta
        co-tc
        wh-qp
        tb-vc
        td-yn
        """.trimIndent()

    override val resultExample1: String = "7"

    override val resultReal1: String = "1200"

    override val resultExample2: String = "co,de,ka,ta"

    override val resultReal2: String = ""

    override val input: String = "/day23/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
