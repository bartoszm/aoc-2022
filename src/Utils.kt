import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("data", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun <T> List<T>.toPair(): Pair<T, T> {
    require(this.size == 2) { "List ${this} is not of length 2!" }
    val (a, b) = this
    return Pair(a, b)
}

fun <T> to2DList(input: List<String>, converter: (String) -> List<T>) = input.map { converter(it) }

class Memoize<in T, out R>(val f: (T) -> R) : (T) -> R {
    private val values = mutableMapOf<T, R>()
    override fun invoke(v: T): R {
        return values.getOrPut(v) {
            val r = f(v)
            r
        }
    }
}

val LongRange.Companion.empty: LongRange
    get() = LongRange(0, -1)

fun LongRange.common(b: LongRange): LongRange {
    val (first, second) = if (this.first < b.first) this to b else b to this
    return if (second.first in first) {
        val l = if (second.last > first.last) first.last else second.last
        second.first..l
    } else {
        LongRange.empty
    }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize(this)