package day14

import readInput
import toPair

fun main() {

    println(solve1(parse(readInput("day14/test")), 500 to 0))
    println(solve1(parse(readInput("day14/input")), 500 to 0))

    println(solve2(parse(readInput("day14/test")), 500 to 0))
    println(solve2(parse(readInput("day14/input")), 500 to 0))
}

typealias Point = Pair<Int, Int>
typealias  Line = List<Point>
typealias Board = Array<IntArray>


fun solve1(rocks: List<Line>, start: Point): Int {
    val simulation = Simulation(fillBoard(rocks))
    var sandUnits = 0

    while (true) {
        val landed = simulation.drop(start)

        if (landed.second == simulation.bottom) {
            return sandUnits
        }
        sandUnits += 1
    }
}

fun solve2(rocks: List<Line>, start: Point): Int {
    val simulation = Simulation(fillBoard(rocks))
    var sandUnits = 0

    while (true) {
        val landed = simulation.drop(start)

        if (landed == start) {
            print(simulation)
            return sandUnits + 1
        }
        sandUnits += 1
    }
}

class Simulation(val board: Board) {

    private fun next(p: Point): Point? {
        return sequenceOf(
            p.first to p.second + 1,
            p.first - 1 to p.second + 1,
            p.first + 1 to p.second + 1

        )
            .filter { it.second <= bottom } //rows
            .filter { it.first in board[0].indices } //columns
            .firstOrNull { board[it.second][it.first] == 0 }

    }

    val bottom: Int by lazy {
        val x = board.withIndex()
            .filter { (_, v) -> v.any { c -> c > 0 } }
            .map { it.index }
            .max()
        x + 1
    }

    fun simulate(from: Point): Point {
        tailrec fun traverse(p: Point): Point {
            val n = next(p)
            return if (n == null) p else traverse(n)
        }
        return traverse(from)
    }


    fun mark(p: Point) {
        board[p.second][p.first] = 1
    }

    fun drop(from: Point): Point {
        val landed = simulate(from)
        mark(landed)
        return landed
    }
}

fun print(s: Simulation) {
    s.board.forEach { row ->
        val r = row.map {
            when (it) {
                0 -> '.'
                1 -> 'o'
                2 -> '#'
                else -> throw java.lang.IllegalStateException()
            }
        }.joinToString("")
        println(r)
    }
}

fun fillBoard(input: List<Line>): Board {
    val board = (0 until 200).map {
        IntArray(1000) { 0 }
    }.toTypedArray()

    fun prog(a: Int, b: Int) = if (a > b) (a downTo b) else (a..b)

    fun fillRow(a: Point, b: Point) {
        if (a.first == b.first) {
            prog(a.second, b.second).forEach { board[it][a.first] = 2 }
        }
    }

    fun fillColumn(a: Point, b: Point) {
        if (a.second == b.second) {
            prog(a.first, b.first).forEach { board[a.second][it] = 2 }
        }
    }

    fun fill(l: Line) {
        l.windowed(2).map { it.toPair() }.forEach { (a, b) ->
            fillColumn(a, b)
            fillRow(a, b)
        }
    }

    input.forEach { fill(it) }
    return board
}

fun parse(input: List<String>) = input.map { parse(it) }

fun parse(i: String): Line {
    fun point(s: String): Point = s.split(",").map { it.trim().toInt() }.toPair()
    return i.split("->").map { point(it) }
}