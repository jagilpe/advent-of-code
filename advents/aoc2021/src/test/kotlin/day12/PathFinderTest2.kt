package com.gilpereda.adventsofcode.adventsofcode2021.day12

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PathFinderTest2 : BaseTest() {
    override val example: String = """
        fs-end
        he-DX
        fs-he
        start-DX
        pj-DX
        end-zg
        zg-sl
        zg-pj
        pj-he
        RW-he
        fs-DX
        pj-RW
        zg-RW
        start-pj
        he-WI
        zg-he
        pj-fs
        start-RW
    """.trimIndent()

    override val result: String = "3509"

    override val input: String = "/day12/input.txt"

    override val run: (Sequence<String>) -> String = ::numberOfPath2
}