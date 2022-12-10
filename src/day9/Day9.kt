package day9

import readInput
import toPair
import kotlin.math.absoluteValue
import kotlin.math.sign


fun main() {
    val testInput = parse(readInput("day09/test"))
    val input = parse(readInput("day09/input"))

    println("${solve1(testInput)} , ${solve2(testInput, 1)}")
    println("${solve1(input)} , ${solve2(input, 1)}")

    println(solve2(testInput, 9))
    println(solve2(input, 9))
}

fun next(pKnot : Pos, thisKnot: Pos) : Pos {
    val x = pKnot.first - thisKnot.first
    val y = pKnot.second - thisKnot.second

    return if(x.absoluteValue < 2 && y.absoluteValue < 2) { thisKnot }
    else {
        thisKnot.first + x.sign to thisKnot.second + y.sign
    }
}

fun solve1(moves: List<Move>): Int {

    val tailVisited = hashSetOf<Pos>()
    val head = 0 to 0
    var tail = 0 to 0

    for(p in (sequenceOf(head) + positions(head, moves))) {
        tailVisited.add(tail)
        tail = next(p, tail)
    }
    tailVisited.add(tail)

    return tailVisited.size
}

fun positions(initial: Pos, moves: List<Move>): Sequence<Pos> {
    var current = initial

    return sequence {
        for(m in moves) {
            val next = m.apply(current)
            current = next.last()
            yieldAll(next)
        }
    }
}

fun solve2(moves: List<Move>, knotsNo: Int): Int {
    val head = 0 to 0
    val tailVisited = hashSetOf<Pos>()
    var knots = Array(knotsNo) {0 to 0}.toList()

    fun move(s: Pos): List<Pos> {
        var prev = s
        val r= sequence {
            for(k in knots) {
                val n = next(prev, k)
                prev = n
                yield(n)
            }
        }.toList()
        return r
    }

    for(p in (sequenceOf(head) + positions(head, moves))) {
        tailVisited.add(knots.last())
        knots = move(p)
    }
    tailVisited.add(knots.last())

    return tailVisited.size
}

typealias Pos = Pair<Int, Int> /* first horizontal (x), second vertical (y) */

enum class Dir {
    L, R, U, D
}

data class Move(val steps: Int, val dir: Dir) {
    fun apply(p: Pos): Sequence<Pos> {
        return when(dir) {
            Dir.L -> (1 .. steps).map { p.first + it to p.second }
            Dir.R -> (1 .. steps).map { p.first - it to p.second }
            Dir.U -> (1 .. steps).map { p.first to p.second + it }
            Dir.D -> (1 .. steps).map { p.first to p.second - it }
        }.asSequence()
    }
}

fun parse(input: List<String>): List<Move> {
    fun map(s: String) : Move {
        val (d, st) = s.split("""\s+""".toRegex()).toPair()
        return Move(st.toInt(), Dir.valueOf(d))
    }

    return input.map{map(it)}
}
