package day2

import readInput
import toPair


fun main() {
    val testInput = parse(readInput("day02/test"))
    val input = parse(readInput("day02/input"))

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun parse(input: List<String>): List<Pair<String, String>> {
    return input.map { i -> i.split("\\s+".toRegex()).map { it.trim() }.toPair() }
}

enum class Score(val score: Int) {
    Loose(0), Tie(3), Win(6)
}

enum class Hand(val points: Int) {
    Rock(1), Paper(2), Scissors(3);

    fun better(): Hand = Hand.values()[(this.ordinal + 1) % 3]
    fun worse(): Hand = Hand.values()[(this.ordinal - 1 + 3) % 3]

    fun play(other: Hand): Score {

        return when (other) {
            this -> Score.Tie
            this.better() -> Score.Loose
            else -> Score.Win
        }
    }
}

val theyEncoding = mapOf(
    "A" to Hand.Rock,
    "B" to Hand.Paper,
    "C" to Hand.Scissors
)

fun solve1(input: List<Pair<String, String>>): Int {
    val mineEncoding = mapOf(
        "X" to Hand.Rock,
        "Y" to Hand.Paper,
        "Z" to Hand.Scissors
    )

    fun score(them: Hand, you: Hand) = you.points + you.play(them).score

    return input.map { (them, you) ->
        theyEncoding[them]!! to mineEncoding[you]!!
    }.sumOf { score(it.first, it.second) }
}

fun solve2(input: List<Pair<String, String>>): Int {
    fun score(v: String): Score {
        return when (v) {
            "X" -> Score.Loose
            "Y" -> Score.Tie
            "Z" -> Score.Win
            else -> throw IllegalStateException()
        }
    }

    fun score(them: Hand, result: String): Int {
        val sc = score(result)

        val mine = when (sc) {
            Score.Win -> them.better()
            Score.Tie -> them
            Score.Loose -> them.worse()
        }
        return sc.score + mine.points
    }

    return input.map { (them, result) ->
        theyEncoding[them]!! to result
    }.sumOf { score(it.first, it.second) }
}