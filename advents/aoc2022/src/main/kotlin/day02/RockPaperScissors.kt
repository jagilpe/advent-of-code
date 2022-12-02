package com.gilpereda.aoc2022.day02

import java.lang.Exception

fun rockPaperScissors(input: Sequence<String>): String =
    input.map {
        val (other, me) = it.split(" ").map { Draw.valOf(it) }
        me.result(other)
    }.toList().sum().toString()

fun rockPaperScissors2(input: Sequence<String>): String =
    input.map {
        val (first, second) = it.split(" ")
        Draw.valOf(first).forResult(second)
    }.toList().sum().toString()

enum class Draw(val result: (Draw) -> Int, val forResult: (String) -> Int) {
    Rock({ other ->
        when (other) {
            Rock -> 4
            Paper -> 1
            Scissors -> 7
        }
    }, {
        when (it) {
            "X" -> 3
            "Y" -> 4
            "Z" -> 8
            else -> throw Exception()
        }
    }
    ),
    Paper({ other ->
        when (other) {
            Rock -> 8
            Paper -> 5
            Scissors -> 2
        }
    },{
        when (it) {
            "X" -> 1
            "Y" -> 5
            "Z" -> 9
            else -> throw Exception()
        }
    }),
    Scissors({ other ->
        when (other) {
            Rock -> 3
            Paper -> 9
            Scissors -> 6
        }
    },{
        when (it) {
            "X" -> 2
            "Y" -> 6
            "Z" -> 7
            else -> throw Exception()
        }
    });

    companion object {
        fun valOf(char: String): Draw =
            when (char) {
                "A", "X" -> Rock
                "B", "Y" -> Paper
                "C", "Z" -> Scissors
                else -> throw Exception()
            }
    }
}