package day13

import readInput

fun main() {

    println(solve1(parse(readInput("day13/test"))))
    println(solve1(parse(readInput("day13/input"))))

    println(solve2(parse(readInput("day13/test"))))
    println(solve2(parse(readInput("day13/input"))))
}

fun compare(left: Element, right: Element): Int {
    fun compareComp(left: Composite, right: Composite): Int {
        val lc = left.children
        val rc = right.children

        for (i in lc.indices) {
            if (i >= rc.size) return 1
            val r = compare(lc[i], rc[i])
            if (r != 0) return r
        }

        return if (lc.size == rc.size) 0 else -1
    }

    return if (left is Leaf) {
        if (right is Leaf) left.v - right.v else compare(Composite(left), right)
    } else {
        if (right is Leaf) compare(left, Composite(right)) else compareComp(
            left as Composite,
            right as Composite
        )
    }
}

fun solve2(input: List<Element>): Int {

    val toSort = (input + parse(listOf("[[2]]", "[[6]]")))

    val sorted = toSort.sortedWith { a, b -> compare(a, b) }

    val f = sorted.indexOfFirst { it.toString() == "[[2]]" } + 1
    val s = sorted.indexOfFirst { it.toString() == "[[6]]" } + 1

    return f * s
}

fun solve1(input: List<Element>): Int {

    val results = input.chunked(2).mapIndexed { idx, (l, r) ->
        idx + 1 to (compare(l, r) < 0)
    }

    return results.filter { it.second }.sumOf { it.first }

}

sealed class Element()
class Leaf(val v: Int) : Element() {
    override fun toString(): String {
        return "$v"
    }
}

class Composite(val children: List<Element>) : Element() {
    constructor(e: Element) : this(listOf(e))

    override fun toString(): String {
        return children.joinToString(",", "[", "]")
    }
}


fun List<Char>.toInt() = this.joinToString("").toInt()

fun parse(i: Iterator<Char>): Element {
    val elem = mutableListOf<Element>()
    val number = mutableListOf<Char>()
    while (i.hasNext()) {
        val c = i.next()
        when (c) {
            ']' -> {
                if (number.isNotEmpty()) {
                    val t = number.joinToString(separator = "")

                    elem.add(Leaf(t.toInt()))
                    number.clear()
                }
                return Composite(elem)
            }
            '[' -> elem.add(parse(i))
            else -> if (c.isDigit()) {
                number += c
            } else {
                if (number.isNotEmpty()) {
                    elem.add(Leaf(number.toInt()))
                    number.clear()
                }

            }
        }
    }
    return elem[0]
}

fun parse(input: List<String>): List<Element> {
    return input
        .filter { it.isNotBlank() }
        .map { parse(it.iterator()) }
}

