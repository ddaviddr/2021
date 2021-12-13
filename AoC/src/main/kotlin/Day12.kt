import Day12.parseInput
import Day12.step2

fun main() {
    val connections = parseInput()
    println("$connections")
    val routes = step2(listOf("start"), connections).toSet()
    println("Finished. Possible routes: \n${routes.pretty()}")
    println("Total routes: ${routes.size}")
}

private fun Set<List<String>>.pretty() = joinToString("\n") { route -> route.joinToString("->", "[", "]") }


object Day12 {
    fun parseInput() = inputDay12.split("\n")
        .map { it.split("-") }
        .flatMap { (lhs, rhs) ->
            listOf(lhs to rhs, rhs to lhs)
        }
        .asMapList()

    fun List<Pair<String, String>>.asMapList(): Map<String, List<String>> {
        val map = mutableMapOf<String, List<String>>()
        forEach { (lhs, rhs) ->
            if (rhs != "start" && lhs != "end") {
                map.putIfAbsent(lhs, emptyList())
                map[lhs] = map[lhs]!! + rhs
            }
        }
        return map
    }

    fun step(path: List<String>, connections: Map<String, List<String>>): List<List<String>> {
        println("performing step for path: $path")
        val last = path.last()
        if (last == "end") return listOf(path).also { println("Finished path. Returning") }
        val nextPaths = connections[last]!!.mapNotNull { nextStep ->
            if (nextStep.isBigCave() || nextStep !in path) {
                path + nextStep
            } else null
        }
        return nextPaths.flatMap { newPath -> step(newPath, connections) }
            .also { println("Returning paths: $it") }
    }

    fun step2(
        path: List<String>,
        connections: Map<String, List<String>>,
        smallCaveVisitedTwice: Boolean = false
    ): List<List<String>> {
        println("performing step for path: $path")
        val last = path.last()
        if (last == "end") return listOf(path).also { println("Finished path. Returning") }
        val nextPaths = connections[last]!!.mapNotNull { nextStep ->
            if (nextStep.isBigCave() || nextStep !in path) {
                path + nextStep to smallCaveVisitedTwice
            } else if (!smallCaveVisitedTwice) path + nextStep to true
            else null
        }
        return nextPaths.flatMap { (newPath, newPathVisitsSmallCaveTwice) -> step2(newPath, connections, newPathVisitsSmallCaveTwice) }
    }

    private fun String.isBigCave() = uppercase() == this

    private val inputDay12 = """
        OU-xt
        hq-xt
        br-HP
        WD-xt
        end-br
        start-OU
        hq-br
        MH-hq
        MH-start
        xt-br
        end-WD
        hq-start
        MH-br
        qw-OU
        hm-WD
        br-WD
        OU-hq
        xt-MH
        qw-MH
        WD-qw
        end-qw
        qw-xt
    """.trimIndent()
}
