fun main() {
    val testInput = parse(readInput("day01/test"))
    val input = parse(readInput("day01/input"))

    println(solve1(testInput))
    println(solve1(input))

    println(solve2(testInput))
    println(solve2(input))
}

fun parse(input: List<String>): List<Int> {
    var currentCount = 0
    return sequence {
        input.asSequence().forEach { iV ->
            if (iV.isEmpty()) {
                yield(currentCount)
                currentCount = 0
            } else {
                currentCount += iV.trim().toInt()
            }
        }
        yield(currentCount)
    }.filter { it != 0 }.toList()
}

fun solve1(input: List<Int>): Int {
    return input.max()
}

fun solve2(input: List<Int>): Int {
    return input.sortedDescending().take(3).sum()
}