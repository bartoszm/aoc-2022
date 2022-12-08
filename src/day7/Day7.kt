package day7

import readInput
import java.util.*


fun main() {
    val testInput = readInput("day07/test")
    val input = readInput("day07/input")

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun dirSizes(lines: List<String>) : Sequence<Int> {
    val s = Stack<Pair<String, MutableList<Int>>>()

    fun compute(): Int {
        val (_, elems) = s.pop()
        val result = elems.sum()
        if(s.isNotEmpty()) {
            s.peek().second.add(result)
        }
        return result
    }

    fun handleData(l: String) {
        val result = """^(\w+)""".toRegex()
        val (f) = result.find(l)!!.destructured
        if(f != "dir") s.peek().second.add(f.toInt())
    }

    fun withResult(l: String): Boolean {
        val cmd = l.drop(2).split("""\s+""".toRegex())
        if(cmd[0] == "cd") {
            if(".." == cmd[1]) {
                return true
            } else {
                s.push(cmd[1] to mutableListOf())
            }
        }
        return false
    }

    return sequence {
        for(l in lines) {
            if(l.startsWith("$")) {
                if(withResult(l)) { yield(compute()) }
            } else {
                handleData(l)
            }
        }
        while(s.isNotEmpty()) {
            yield(compute())
        }
    }
}

fun solve1(lines: List<String>): Int {
    return dirSizes(lines).filter { it <= 100000 }.sum()
}

fun solve2(lines: List<String>): Int {
    val r = dirSizes(lines).toList()
    val free =  70000000 - r.max()
    val needed = 30000000

    return dirSizes(lines)
        .filter { free + it >= needed }.min()
}
