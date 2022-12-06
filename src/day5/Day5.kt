package day5


import readInput



fun main() {
    val testInput = parse(readInput("day05/test"))
    val input = parse(readInput("day05/input"))

    println(solve1(testInput.first, testInput.second))
    println(solve1(input.first, input.second))

    println(solve2(testInput.first, testInput.second))
    println(solve2(input.first, input.second))
}

fun solve1(stacks: List<Stack>, moves: List<Move>) : String {
    fun push(stack: Stack, content: String) = content.fold(stack) { acc, c -> Stack(c.toString() + acc.content) }
    return solve(stacks, moves, ::push)
}

fun solve2(stacks: List<Stack>, moves: List<Move>) : String {
    fun push(stack: Stack, content: String) = Stack( content +stack.content)
    return solve(stacks, moves, ::push)
}

fun solve(stacks: List<Stack>, moves: List<Move>, crane: (Stack, String) -> Stack): String {

    val subject = stacks.toMutableList()

    for(move in moves) {
        val f = move.from - 1
        val t = move.to - 1

        subject[t] = crane(subject[t], subject[f].take(move.quantity))
        subject[f] = subject[f].drop(move.quantity)

    }
    return subject.map { it.content[0] }.joinToString("")
}

data class Stack(val content: String) {
    fun take(size: Int) = content.substring(0, size)
    fun drop(size: Int) = Stack(content.substring(size))
}
data class Move(val from: Int, val to: Int, val quantity: Int)

fun parse(input: List<String>): Pair<List<Stack>, List<Move>> {
    fun parseStacks(v: String): List<Stack> {
        return v.chunked(4)
            .map{if(it.isBlank()) Stack("") else Stack("" + it[1])}
    }

    fun parseMove(v: String): Move? {
        if(!v.startsWith("move")) {
            return null
        }
        val tokens = v.split("""\s+""".toRegex()).map { it.trim() }
        return Move(from = tokens[3].toInt(), to = tokens[5].toInt(), quantity = tokens[1].toInt())
    }

    val seq = input.asSequence()

    val stacks = seq.takeWhile { it.trim().startsWith('[') }
        .map { parseStacks(it) }
        .fold(listOf<Stack>()) { acc, ns ->  merge(ns, acc) }

    val moves = seq.mapNotNull { parseMove(it) }.toList()
    return stacks to moves
}

fun merge(newStack: List<Stack>, currentStack: List<Stack>): List<Stack> {
    val merged = (currentStack zip newStack).map { (c,n) -> Stack(c.content + n.content)  }
    val (long, short) = if(newStack.size > currentStack.size) {
        newStack to currentStack
    } else {
        currentStack to newStack
    }

    return merged + long.subList(short.size, long.size)

}
