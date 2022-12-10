package day10

import readInput
import toPair


fun main() {
    val testInput = parse(readInput("day10/test"))
    val input = parse(readInput("day10/input"))

    println("${solve1(testInput)} ${solve1gen(testInput)}")
    println("${solve1(input)} ${solve1gen(input)}")
    

    solve2(testInput).forEach { println(it) }
    println("-----")
    solve2(input).forEach { println(it) }
    println("-----")
    solve2gen(input).forEach { println(it) }

}

fun execute(v: Int, cycle: Int, cmd: Cmd): Sequence<Pair<Int, Int>> {
    return when(cmd){
        Noop -> sequenceOf(v to cycle + 1)
        is AddX -> sequenceOf(v to cycle + 1, v + cmd.value to cycle + 2)
    }
}

fun solve1gen(input: List<Cmd>): Int {
    return process(1 to 1, input)
        .filter { (_, c) -> (c - 20) % 40 == 0 }
        .takeWhile { (_,c) -> c <= 220 }
        .sumOf { (v,c) -> v * c }
}

fun solve2gen(input: List<Cmd>): List<String> {
    val size = 40
    return process(0 to 0, input)
        .map {(value, cycle) -> if (cycle % size in value..value + 2) '#' else '.' }
        .chunked(size)
        .map { c -> c.joinToString("") }.toList()
}

fun process(init: Pair<Int,Int>, cmds: List<Cmd>): Sequence<Pair<Int, Int>> {
    val iter = cmds.iterator()
    return generateSequence(seed = sequenceOf(init)) { last ->
        val (v,c) = last.last()
        if(iter.hasNext()) execute(v,c, iter.next()) else null
    }.flatMap { it }
}

fun solve1(input: List<Cmd>): Int {

    var cycle = 1
    var value = 1
    var emmit = 20

    val emmits = sequence {
        for (cmd in input) {
            for ((v, c) in execute(value, cycle, cmd)) {
                if (cycle == emmit) {
                    yield(value to cycle)
                    emmit += 40
                }
                cycle = c
                value = v
            }
        }
    }

    return emmits.takeWhile{ it.second <= 220}.map{(v,c) -> v * c}.sum()
}


fun solve2(input: List<Cmd>): List<String> {
    var cycle = 0
    var value = 0
    val size = 40

     return sequence {
        for (cmd in input) {
            for ((v, c) in execute(value, cycle, cmd)) {
                yield(if (cycle % size in value..value + 2) '#' else '.')
                cycle = c
                value = v

            }
        }
    }.chunked(size).map { c -> c.joinToString("") }.toList()
}


sealed class Cmd(val cycles: Int)
object Noop : Cmd(1)
class AddX(val value: Int) : Cmd(2)

fun parse(input: List<String>): List<Cmd> {
    fun read(s:String): Cmd {
        val (_, v) = s.split("""\s+""".toRegex()).toPair()
        return AddX(v.toInt())
    }
    return input.map {
        if(it == "noop") Noop
        else read(it)
    }
}
