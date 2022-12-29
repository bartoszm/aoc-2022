package day21

import readInput
import toPair


fun main() {

    println(solve1(parse(readInput("day21/test"))))
    println(solve1(parse(readInput("day21/input"))))

    println(solve2(parse(readInput("day21/test"))))
    println(solve2(parse(readInput("day21/input"))))

}

fun String.isIdentifier() = !this[0].isDigit() && this.length > 1

fun convert(exprs: Map<String, List<String>>): Map<String, Operation> {
    val operations = hashMapOf<String, Operation>()

    fun oper(key: String, tokens : List<String>): Operation {
        var o = operations[key]

        if(o == null) {
            o = if(tokens.size == 1) {
                if(tokens[0].isIdentifier()) {
                    oper(tokens[0], exprs[tokens[0]]!!)
                } else {
                    Const(tokens[0].toLong())
                }

            }
            else {
                val(lT, opr, rT) = tokens

                val left = oper(lT, exprs[lT]!!)
                val right = oper(rT, exprs[rT]!!)

                when(opr) {
                    "+" -> Function(left, right) {a,b -> a + b}
                    "-" -> Function(left, right) {a,b -> a - b}
                    "*" -> Function(left, right) {a,b -> a * b}
                    "/" -> Function(left, right) {a,b -> a / b}
                    else -> error("Operation $opr is not supported")
                }
            }
            operations[key] = o
        }

        return o
    }

    exprs.forEach { (t, u) -> oper(t,u) }

    return operations
}

fun solve1(exprs: Map<String, List<String>>): Long {
    val operations = convert(exprs)
    return operations["root"]?.invoke()!!
}

typealias Expr = Pair<String, List<String>>


fun unresolved(exprs: Map<String, List<String>>) = exprs.values.asSequence()
    .flatMap { it.filter { e -> e.isIdentifier() } }
    .filter { !exprs.containsKey(it) }
    .toSet()
fun transform(from: String, exprs: Map<String, List<String>>) : Map<String, List<String>> {
    fun String.isFirst(expr: List<String>) = this == expr[0]

    fun map(what: String, expr: Expr): Expr {
        val (lhs, old) = expr
        require(old.size == 3)

        if(lhs == "root") {
            return what to listOf(if(what.isFirst(old)) old[2] else old[0])
        }

        return what to when(old[1]) {
            "*" -> if(what.isFirst(old)) listOf(lhs, "/", old[2]) else listOf(lhs, "/", old[0])
            "+" -> if(what.isFirst(old)) listOf(lhs, "-", old[2]) else listOf(lhs, "-", old[0])
            "/" -> if(what.isFirst(old)) listOf(lhs, "*", old[2]) else listOf(old[0], "/", lhs)
            "-" -> if(what.isFirst(old)) listOf(lhs, "+", old[2]) else listOf(old[0], "-", lhs)
            else -> error("not supported operator ${old[1]}")
        }
    }

    fun find(name: String) = exprs.asSequence()
        .map { it.toPair() }
        .first { (_, v) -> v.contains(name) }

    fun pathToRoot() : List<Pair<String, Expr>> {
        var e = from
        return sequence {
            while (e != "root") {
                val found = find(e)
                yield(e to  found)
                e = found.first
            }
        }.toList()
    }

    val trans = hashMapOf<String, List<String>>()

    val modified = pathToRoot().map { (what, expr) -> map(what, expr) }
    modified.forEach { (k, v) -> trans[k] = v  }

    var unresolved = unresolved(trans)

    while (unresolved.isNotEmpty()) {
        unresolved
            .map { u -> u to exprs[u]!! }
            .forEach { (k,v) -> trans[k] = v }
        unresolved = unresolved(trans)
    }

    return trans
}

fun solve2(exprs: Map<String, List<String>>): Long {
    val operations = convert(transform("humn", exprs))
    return operations["humn"]?.invoke()!!
}


sealed interface Operation {
    operator fun invoke() :Long

}

class Function(val a: Operation, val b: Operation, val f: (Long,Long) -> Long) : Operation {
    override fun invoke() = f.invoke(a(), b())
}

class Const(val value: Long) : Operation {
    override fun invoke() = value

}

class Subst(val o: Operation) : Operation by o

fun parse(line: String) = line.split(":").map { it.trim() }.toPair()
        .let { (k,v) ->  k to v.split(" ") }

fun parse(input: List<String>)= input.associate { parse(it) }
