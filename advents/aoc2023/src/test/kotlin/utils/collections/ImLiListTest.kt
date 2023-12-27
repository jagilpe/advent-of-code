package com.gilpereda.aoc2022.utils.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ImLiListTest {

    @Test
    fun `should be able to build one`() {
        assertThat(ImLiList.singleton(1)).isNotNull
        assertThat(ImLiList.singleton("test")).isNotNull

    }
}