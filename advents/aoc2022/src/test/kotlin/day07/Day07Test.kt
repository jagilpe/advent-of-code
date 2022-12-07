package com.gilpereda.aoc2022.day07

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Part 2 : 24390891 -> Too high
 */
class Day07Test : BaseTest() {
    override val example: String = """
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k"""".trimIndent()

    override val result1: String
        get() = "95437"

    override val result2: String
        get() = "24933642"

    override val input: String = "/day07/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask


//    @Test
//    fun `should find the cd command`() {
//        assertThat(parseLine("${'$'} cd /")).isEqualTo(Cd("/"))
//    }
//
//    @Test
//    fun `should find the ls command`() {
//        assertThat(parseLine("${'$'} ls")).isEqualTo(Ls)
//    }
//
//    @Test
//    fun `should find the dir output`() {
//        assertThat(parseLine("dir e")).isEqualTo(DirOutput("e"))
//    }
//
//    @Test
//    fun `should find the file output`() {
//        assertThat(parseLine("4060174 j")).isEqualTo(FileOutput(4060174))
//    }

}
