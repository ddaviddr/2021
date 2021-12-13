import kotlin.math.max
import kotlin.math.min

fun main() {
    val map = parse()
    println("Map: \n${map.pretty()}")
    val blinkedList = mutableListOf<Set<Pair<Int, Int>>>()
    var count = 1
    val totalSize = map.size * map.first().size
    do {
        val blinked = step(map)
        println("Map after step $count: \n${map.pretty()}")
        blinkedList.add(blinked)
        count++
    } while (blinked.size < totalSize)
    println("Times blinked: ${blinkedList.sumOf { it.size }}")
}

private fun List<List<Int>>.pretty() = joinToString("\n") { line -> line.joinToString() }

fun step(map: List<MutableList<Int>>): Set<Pair<Int, Int>> {
    val alreadyBlinked = mutableSetOf<Pair<Int, Int>>()
    map.forEachIndexed { y, rows ->
        rows.forEachIndexed { x, _ ->
            map[y][x]++
            if (map[y][x] > 9) {
                processBlink(x, y, map, alreadyBlinked)
            }
        }
    }
    map.resetBlinked(alreadyBlinked)
    return alreadyBlinked
}

private fun List<MutableList<Int>>.resetBlinked(alreadyBlinked: MutableSet<Pair<Int, Int>>) =
    alreadyBlinked.forEach { (x, y) ->
        this[y][x] = 0
    }

fun processBlink(
    blinkedX: Int,
    blinkedY: Int,
    map: List<MutableList<Int>>,
    alreadyBlinked: MutableSet<Pair<Int, Int>>
) {
    if (alreadyBlinked.add(blinkedX to blinkedY)) {
        for (y in max(0, blinkedY - 1)..min(map.lastIndex, blinkedY + 1)) {
            for (x in max(0, blinkedX - 1)..min(map.first().lastIndex, blinkedX + 1)) {
                map[y][x]++
                if (map[y][x] > 9) processBlink(x, y, map, alreadyBlinked)
            }

        }
    }
}

private fun parse() = input.split("\n").map { line ->
    line.map { it.digitToInt() }.toMutableList()
}

val input = """
    6227618536
    2368158384
    5385414113
    4556757523
    6746486724
    4881323884
    4648263744
    4871332872
    4724128228
    4316512167
""".trimIndent()