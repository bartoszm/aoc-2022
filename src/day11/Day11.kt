package day11

import readInput
import java.util.LinkedList


fun main() {
    println( solve1(testMonkeys()))
    println( solve1(taskMonkeys()))

    println( solve2(testMonkeys()))
    println( solve2(taskMonkeys()))
}

typealias Action = (Long) -> Unit

class Monkey(val name: String,
             initial: List<Long>,
             private val operation: (Long) -> Long,
             val denom: Long,
             val positive : Action,
             val negative: Action) {
    var inspected = 0L
    private var levels = LinkedList(initial)
    private lateinit var worryReducer: (Long) -> Long

    fun receive(item : Long) {
        levels.add(item)
    }

    private fun test(v: Long): Boolean = v % denom == 0L

    fun calm(worryReducer: (Long) -> Long): Monkey {
        this.worryReducer = worryReducer
        return this
    }

    fun round() {
        val insp = levels.map(operation)
        inspected += insp.size
        levels.clear()
        insp.asSequence()
            .map{worryReducer(it)}
            .forEach{
                if(test(it)) {
                    positive(it)
                } else {
                    negative(it)
                }
            }
    }
}

fun solve(monkeys: List<Monkey>, rounds: Int): Long {
    for(i in 1..rounds) {
        monkeys.forEach { it.round() }
    }
    val active = monkeys.sortedByDescending { it.inspected }.take(2)
    return active.map { it.inspected }.reduce{ x,y -> x*y }
}

fun solve1(monkeys: List<Monkey>): Long {
    monkeys.forEach { it.calm { v -> v / 3 } }
    return solve(monkeys, 20)
}

fun solve2(monkeys: List<Monkey>): Long {
    val d = monkeys.map { it.denom }.reduce { a, b -> a*b }
    monkeys.forEach { it.calm { v -> v % d } }
    return solve(monkeys, 10_000)
}

fun taskMonkeys(): List<Monkey> {
    return parse(readInput("day11/input"))
}

private fun testMonkeys(): List<Monkey> {
    return parse(readInput("day11/test"))
}

fun parse(lines: List<String>): List<Monkey> {
    val ctx = mutableMapOf<String, Monkey>()

    val monkeys = lines.chunked(7)
        .map { parseMonkey(it) { n -> ctx[n]!! } }
    ctx.putAll(monkeys.associateBy { it.name })
    return monkeys
}

val nameRegexp = """\s(\d+):""".toRegex()
val nameTest = """throw to monkey (\d+)""".toRegex()

fun parseMonkey(rows: List<String>, sel: (String) -> Monkey): Monkey {
    val name = nameRegexp.find(rows[0])!!.groupValues[1]
    val initial = toItems(rows[1])
    val modifier = toModifier(rows[2])
    val divisor = rows[3].split("by")[1].trim().toLong()
    val positive = nameTest.find(rows[4])!!.groupValues[1]
    val negative = nameTest.find(rows[5])!!.groupValues[1]

    return Monkey(name, initial, modifier, divisor,
        {v -> sel(positive).receive(v)},
        {v -> sel(negative).receive(v)})
}


fun toModifier(str: String): (Long) -> Long {
    var old : Long = 0

    fun toOper(s: String): (Long, Long) -> Long = when(s) {
        "+" -> {a,b -> a + b}
        "*" -> {a, b -> a * b}
        else -> throw IllegalArgumentException("$s not supported")
    }

    fun toV(s: String): () -> Long {
        return if(s == "old") { -> old}
        else { -> s.toLong() }
    }

    val tokens = str.split(" = ")[1].split(" ").map { it.trim() }

    val first = toV(tokens[0])
    val second = toV(tokens[2])
    val f = toOper(tokens[1])

    return {x ->
        old = x
        f(first.invoke(), second.invoke())
    }
}

fun toItems(s: String): List<Long> {
    return s.split(":")[1]
        .split(",").map { it.trim().toLong() }
}
