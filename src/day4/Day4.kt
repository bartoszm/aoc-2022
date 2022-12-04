package day4

import readInput
import toPair

fun main() {
    val testInput = parse(readInput("day04/test"))
    val input = parse(readInput("day04/input"))

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun solve1(input: List<Pair<IntRange, IntRange>>): Int {
    fun IntRange.contains(r: IntRange) = r.first in this && r.last in this
    fun computePair(a: IntRange, b: IntRange) = if (a.contains(b) || b.contains(a)) 1 else 0

    return input.sumOf { (a, b) -> computePair(a, b) }
}

fun solve2(input: List<Pair<IntRange, IntRange>>): Int {
    fun computePair(a: IntRange, b: IntRange) = if ((a intersect b).isEmpty()) 0 else 1
    return input.sumOf { (a, b) -> computePair(a, b) }
}

fun parse(input: List<String>): List<Pair<IntRange, IntRange>> {
    fun toRange(s: String): IntRange {
        val (a, b) = s.split("-").map { c -> c.trim().toInt() }.toPair()
        return a..b
    }

    fun parseLine(v: String): Pair<IntRange, IntRange> {
        return v.split(',')
            .map { toRange(it) }
            .toPair()
    }

    return input.map { parseLine(it) }
}
