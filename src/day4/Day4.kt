package day4

import readInput
import toPair

data class Range(val start: Int, val end: Int)

fun main() {
    val testInput = parse(readInput("day04/test"))
    val input = parse(readInput("day04/input"))

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun solve1(input: List<Pair<Range, Range>>): Int {
    fun Range.contains(r: Range) = this.start <= r.start && this.end >= r.end
    fun computePair(a: Range, b: Range) = if (a.contains(b) || b.contains(a)) 1 else 0

    return input.sumOf { (a, b) -> computePair(a, b) }
}

fun solve2(input: List<Pair<Range, Range>>): Int {
    fun Range.disjoint(r: Range): Boolean {
        return when {
            this.start < r.start -> this.end < r.start
            this.end > r.end -> this.start > r.end
            else -> false
        }
    }

    fun computePair(a: Range, b: Range) = if (a.disjoint(b)) 0 else 1

    return input
        .sumOf { (a, b) -> computePair(a, b) }
}

fun parse(input: List<String>): List<Pair<Range, Range>> {
    fun toRange(s: String): Range {
        val (a, b) = s.split("-").map { c -> c.trim().toInt() }.toPair()
        return Range(a, b)
    }

    fun parseLine(v: String): Pair<Range, Range> {
        return v.split(',')
            .map { toRange(it) }
            .toPair()
    }

    return input.map { parseLine(it) }
}
