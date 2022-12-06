package day6


import readInput



fun main() {

    println(solve("bvwbjplbgvbhsrlpgdmjqwftvncz",4))
    println(solve("nppdvjthqldpwncqszvftbrmjlhg", 4))
    println(solve("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4))
    println(solve("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4))
    println(solve(readInput("day06/input")[0], 4))
    println(solve("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 14))
    println(solve("bvwbjplbgvbhsrlpgdmjqwftvncz", 14))
    println(solve(readInput("day06/input")[0], 14))
}

fun solve(buffer: String, unique: Int = 4): Int {
    val found = buffer.windowed(unique, 1) {it.toSet()}
        .indexOfFirst { it.size == unique}
    return if(found > -1) found + unique else -1
}

