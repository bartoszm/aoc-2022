package day3

import readInput


fun main() {
    val testInput = parse(readInput("day03/test"))
    val input = parse(readInput("day03/input"))

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun position(c: Char): Int {
    return if (c.isLowerCase()) {
        c.code - 'a'.code + 1
    } else {
        c.code - 'A'.code + 27
    }
}

fun solve1(input: List<CharArray>): Int {
    fun translate(a: CharArray): Pair<List<Char>, List<Char>> {
        return a.toList().subList(0, a.size / 2) to a.toList().subList(a.size / 2, a.size)
    }

    return input
        .map { translate(it) }.sumOf { (l, r) ->
            val common = l.toSet() intersect r.toSet()
            require(common.size == 1)
            position(common.first())
        }
}

fun solve2(input: List<CharArray>): Int {
    return input.chunked(3)
        .map {
            it.map { x -> x.toSet() }
                .reduce { acc, v -> acc intersect v }
                .also { v -> require(v.size == 1) }
        }
        .sumOf { v -> position(v.first()) }
}

fun parse(input: List<String>): List<CharArray> {
    return input
        .map { it.toCharArray() }
}
