package day15

import readInput
import kotlin.math.absoluteValue

typealias Point = Pair<Long, Long>

fun main() {

    println(solve1(parse(readInput("day15/test")), 10))
    println(solve1(parse(readInput("day15/input")), 2000000))

    println(solve2(parse(readInput("day15/test")), 20))
    println(solve2(parse(readInput("day15/input")), 4000000))
}

fun taxi(a: Point, b: Point) = (a.first - b.first).absoluteValue + (a.second - b.second).absoluteValue

class Sensor(val loc: Point, beac: Point) {
    val distance = taxi(loc, beac)
    fun canSense(p: Point) = taxi(loc, p) > distance
    fun coverageIn(row: Long): LongRange {
        val rem = distance - (loc.second - row).absoluteValue
        return if (rem < 0L) LongRange.EMPTY
        else loc.first - rem..loc.first + rem
    }
}

fun toDist(p: Pair<Point, Point>, c: (Long, Long) -> Long) = c(p.first.first, taxi(p.first, p.second))

fun minOf(values: List<Pair<Point, Point>>): Long {
    return values.minOf { toDist(it) { a, b -> a - b } }
}

fun maxOf(values: List<Pair<Point, Point>>): Long {
    return values.maxOf { toDist(it) { a, b -> a + b } }
}

fun solve1(data: List<Pair<Point, Point>>, row: Long): Int {
    val sensors = data.map { Sensor(it.first, it.second) }

    val start = minOf(data)
    val stop = maxOf(data)

    fun beaconsAt(row: Long) = data.map { it.second }.distinct()
        .filter { it.second == row }
        .count { it.first in start..stop }

    fun impossible(p: Point) = sensors.any { !it.canSense(p) }

    return (start..stop).map { it to row }
        .count { impossible(it) } - beaconsAt(row)
}

fun combine(ranges: List<LongRange>): List<LongRange> {

    fun sum(a: LongRange, b: LongRange): List<LongRange> {
        if (a.last < b.first) return listOf(a, b)
        return listOf(if (a.last >= b.last) a else a.first..b.last)
    }

    val r = ranges.sortedBy { it.first }
    return r.fold(listOf(r.first())) { acc, v ->
        acc.dropLast(1) + sum(acc.last(), v)
    }
}

fun toValues(a: LongRange, b: LongRange) = (a.last + 1 until b.first).toList()
fun toValues(ranges: List<LongRange>) = ranges.windowed(2).flatMap { (a, b) -> toValues(a, b) }


fun solve2(data: List<Pair<Point, Point>>, max: Int): Long {
    val sensors = data.map { Sensor(it.first, it.second) }

    val point = (0..max.toLong()).asSequence().flatMap { row ->
        val coverage = combine(sensors.map { it.coverageIn(row) })
        toValues(coverage).map { v -> v to row }
    }.first()

    return point.first * 4000000 + point.second
}

//Sensor at x=20, y=1: closest beacon is at x=15, y=3
val  cRegex = """x=([\d-]+), y=([\d-]+)""".toRegex()
fun parse(input: List<String>): List<Pair<Point, Point>> {
    return input.map {
        val res = cRegex.find(it)!!
        val (sx, sy) = res.destructured
        val (bx, by) = res.next()!!.destructured
        val s = sx.toLong() to sy.toLong()
        val b = bx.toLong() to by.toLong()
        s to b
    }
}



