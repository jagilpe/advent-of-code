package com.gilpereda.adventsofcode.adventsofcode2021.day07

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class FuelTest1 : BaseTest() {
    override val example: String = "16,1,2,0,4,2,7,1,2,14"

    override val result1: String = "37"
    override val result2: String = "168"

    override val input: String = "/day07/input.txt"

    override val run1: (Sequence<String>) -> String = ::consumedFuel
    override val run2: (Sequence<String>) -> String = ::consumedFuel2
}