package day8

import readInput


fun main() {
    val testInput = parse(readInput("day08/test"))
    val input = parse(readInput("day08/input"))

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun row(arr: Array<IntArray>, idx: Int, ids: IntProgression) : Sequence<Int> {
    require(idx >= 0 && idx < arr.size)
    return ids.map { arr[idx][it] }.asSequence()
}

fun column(arr: Array<IntArray>, idx: Int, ids: IntProgression): Sequence<Int> {
    return ids.map { arr[it][idx] }.asSequence()
}

fun <T> Sequence<T>.takeWhileInclusive(pred: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}

fun solve2(arr: Array<IntArray>): Int {

    fun value(o: Int, i: Int) : Int {
        val v = arr[o][i]
        return sequenceOf(
            row(arr, o, i - 1 downTo 0),
            row(arr, o, i + 1 ..  arr[0].lastIndex),
            column(arr, i, o - 1 downTo 0),
            column(arr, i, o + 1 ..  arr.lastIndex)
        )
            .map {x -> x.takeWhileInclusive { v > it }.count() }
            .reduce{ a,b -> a * b }
    }

    var max  = -1
    for(o in arr.indices) {
        for(i in arr[o].indices) {
            val result = value(o,i)
            if(result > max) max = result
        }
    }
    return max
}


fun solve1(arr: Array<IntArray>): Int {
    var counter = 0

    fun isVisible(o: Int, i: Int): Boolean {
        val v = arr[o][i]

        return sequenceOf(
            row(arr, o, i - 1 downTo 0),
            row(arr, o, i + 1 ..  arr[0].lastIndex),
            column(arr, i, o - 1 downTo 0),
            column(arr, i, o + 1 ..  arr.lastIndex)
        ).map { it.all { x -> x < v } }.any{it}
    }

    for(o in arr.indices) {
        for(i  in arr[0].indices) {
            if(isVisible(o, i)) {
                counter += 1
            }
        }
    }
    return counter
}

fun parse(input: List<String>): Array<IntArray> {
    fun arr(s: String): IntArray = s.map { it.digitToInt() }.toIntArray()

    return input.map{arr(it)}.toTypedArray()
}
