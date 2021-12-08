package day06

import com.gilpereda.adventsofcode.adventsofcode2021.day06.lanternfishes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

val exampleInput = listOf(3, 4, 3, 1, 2)

val input = listOf(2,1,2,1,5,1,5,1,2,2,1,1,5,1,4,4,4,3,1,2,2,3,4,1,1,5,1,1,4,2,5,5,5,1,1,4,5,4,1,1,4,2,1,4,1,2,2,5,1,1,5,1,1,3,4,4,1,2,3,1,5,5,4,1,4,1,2,1,5,1,1,1,3,4,1,1,5,1,5,1,1,5,1,1,4,3,2,4,1,4,1,5,3,3,1,5,1,3,1,1,4,1,4,5,2,3,1,1,1,1,3,1,2,1,5,1,1,5,1,1,1,1,4,1,4,3,1,5,1,1,5,4,4,2,1,4,5,1,1,3,3,1,1,4,2,5,5,2,4,1,4,5,4,5,3,1,4,1,5,2,4,5,3,1,3,2,4,5,4,4,1,5,1,5,1,2,2,1,4,1,1,4,2,2,2,4,1,1,5,3,1,1,5,4,4,1,5,1,3,1,3,2,2,1,1,4,1,4,1,2,2,1,1,3,5,1,2,1,3,1,4,5,1,3,4,1,1,1,1,4,3,3,4,5,1,1,1,1,1,2,4,5,3,4,2,1,1,1,3,3,1,4,1,1,4,2,1,5,1,1,2,3,4,2,5,1,1,1,5,1,1,4,1,2,4,1,1,2,4,3,4,2,3,1,1,2,1,5,4,2,3,5,1,2,3,1,2,2,1,4)

class LanternfishTest1 {
    @Test
    fun `should work with the example`() {
        assertThat(lanternfishes(exampleInput, 80)).isEqualTo(5934)
    }

    @Test
    fun `should work with the example 2`() {
        assertThat(lanternfishes(exampleInput, 256)).isEqualTo(26984457539)
    }

    @Test
    fun `should get the result`() {
        val result = lanternfishes(input, 80)
        assertThat(result).isNotNull

        println("Result: $result")
    }

    @Test
    fun `should get the result 2`() {
        val result = lanternfishes(input, 256)
        assertThat(result).isNotNull

        println("Result: $result")
    }
}