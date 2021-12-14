package com.gilpereda.adventsofcode.adventsofcode2021.day12

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PathFinderTest1 : BaseTest() {
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

    override val result: String = "226"

    override val input: String = "/day12/input.txt"

    override val run: (Sequence<String>) -> String = ::numberOfPaths

    @Test
    fun `should parse a connection`() {
        assertThat(connection("fs-end")).isEqualTo(Pair("fs", "end"))
    }

    @Test
    fun `should create a start node`() {
        assertThat(Node.from("start")).isInstanceOf(Start::class.java)
    }

    @Test
    fun `should create an end node`() {
        assertThat(Node.from("end")).isInstanceOf(End::class.java)
    }

    @Test
    fun `should create a small cave node`() {
        assertThat(Node.from("zg")).isEqualTo(SmallCave("zg"))
    }

    @Test
    fun `should create a big cave node`() {
        assertThat(Node.from("RW")).isEqualTo(BigCave("RW"))
    }
}