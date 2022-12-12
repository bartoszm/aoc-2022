package day12


import readInput
import java.util.*


fun main() {
    println(solve1(parse(readInput("day12/test"))))
    println(solve1(parse(readInput("day12/input"))))

    println(solve2(parse(readInput("day12/test"))))
    println(solve2(parse(readInput("day12/input"))))
}

typealias Point = Pair<Int, Int>


fun Array<CharArray>.weight(p: Point): Char = when (this[p.first][p.second]) {
    'S' -> 'a'
    'E' -> 'z'
    else -> this[p.first][p.second]
}

fun toGraphE(input: Array<CharArray>): Map<Point, List<Pair<Point, Int>>> {
    val edges = hashMapOf<Point, List<Pair<Point, Int>>>()

    for (i in input.indices) {
        for (j in input[0].indices) {
            val thiz = i to j
            edges[thiz] = neighbours(input, thiz)
        }
    }
    return edges
}

fun toPosition(x: Char, input: Array<CharArray>): List<Point> {
    return sequence {
        for (i in input.indices) {
            for (j in input[0].indices) {
                if (input[i][j] == x) {
                    yield(i to j)
                }
            }
        }
    }.toList()
}

fun solve1(input: Array<CharArray>): Int {
    val g = toGraphE(input)
    val start = toPosition('S', input).first()
    val end = toPosition('E', input).first()
    return findShortestPath(start, end, g)
}

fun solve2(input: Array<CharArray>): Int {
    val g = toGraphE(input)
    val start = toPosition('S', input) + toPosition('a', input)
    val end = toPosition('E', input).first()
    return start.map {
        findShortestPath(it, end, g)
    }.filter { it > 0 }.min()
}

fun findShortestPath(
    src: Point,
    dest: Point,
    connections: Map<Point, List<Pair<Point, Int>>>
): Int {

    val visited = hashSetOf<Point>()

    val queue: Queue<Point> = LinkedList()
    visited.add(src)
    queue.add(src)
    val parents = hashMapOf<Point, Point>()

    while (queue.isNotEmpty()) {
        val node = queue.poll()
        visited.add(node)
        if (node == dest) {
            return path(dest, parents).size
        }

        connections[node]!!
            .filter { !visited.contains(it.first) }
            .filter { !queue.contains(it.first) }
            .forEach {
                parents[it.first] = node
                queue.add(it.first)
            }
    }

    return -1
}

fun path(dest: Point, parents: HashMap<Point, Point>): List<Point> {
    var current = dest
    return sequence {
        while (parents[current] != null) {
            yield(parents[current]!!)
            current = parents[current]!!
        }
    }.toList()
}

private fun neighbours(
    input: Array<CharArray>,
    thiz: Pair<Int, Int>
): List<Pair<Point, Int>> {
    return sequenceOf(
        thiz.first - 1 to thiz.second, thiz.first + 1 to thiz.second,
        thiz.first to thiz.second - 1, thiz.first to thiz.second + 1
    )

        .filter { it.first in input.indices && it.second in input[0].indices }
        .map {
            val w = input.weight(it) - input.weight(thiz)
            it to if (w > 0) w else 0
        }
        .filter { it.second in (0..1) }
        .map { (p, v) -> p to v + 1 }.toList()
}

fun parse(input: List<String>) = input.map { it.toCharArray() }.toTypedArray()

