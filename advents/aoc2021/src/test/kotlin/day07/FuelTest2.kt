package com.gilpereda.adventsofcode.adventsofcode2021.day07

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class FuelTest2 : BaseTest() {
    override val example: String = "16,1,2,0,4,2,7,1,2,14"

    override val result: String = "168"

    override val input: String = "/day07/input.txt"

    override val run: (Sequence<String>) -> String = ::consumedFuel2

}