import Day14.calculateSteps

fun main() {
    calculateSteps(10)
    calculateSteps(40)
}

object Day14 {
    fun calculateSteps(steps: Int) {
        val dictionary = buildDictionary()
        var pairs = initialChain.toPairsMap()
        (1..steps).forEach { step ->
            println(pairs)
            println("Performing step $step")
            pairs = pairs.translatePairs(dictionary)
        }

        val frequencies = pairs.printFrequencies()
        val min = frequencies.values.minOrNull()!!
        val max = frequencies.values.maxOrNull()!!
        println("Result: ${max - min}")
    }

    private fun String.toPairsMap(): PairMap {
        val pairs = (0 until lastIndex).map { index ->
            "${this[index]}${this[index + 1]}"
        }
        return pairs.fold(PairMap(mutableMapOf())) { acc, s ->
            val currentCount = acc.map.getOrDefault(s, 0L)
            acc.map[s] = currentCount + 1
            acc
        }
    }

    private fun PairMap.printFrequencies(): Map<String, Long> {
        val firstLetter = "${initialChain[0]}"
        val lastLetter = "${initialChain[initialChain.lastIndex]}"
        val occurrences = mutableMapOf<String, Long>()
        map.forEach { (key, times) ->
            key.forEach { letter ->
                val count = occurrences.getOrDefault("$letter", 0)
                occurrences["$letter"] = count + times
            }
        }
        return occurrences.entries.associate { (key, value) ->
            var currentValue = value
            if (key == firstLetter) currentValue--
            if (key == lastLetter) currentValue--
            currentValue /= 2
            if (key == firstLetter) currentValue++
            if (key == lastLetter) currentValue++
            key to currentValue
        }.also { print(it) }
    }

    fun PairMap.translatePairs(dictionary: Map<String, String>): PairMap {
        val newMap = mutableMapOf<String, Long>()
        map.forEach { (key, times) ->
            val left = "${key[0]}${dictionary[key]}"
            val right = "${dictionary[key]}${key[1]}"
            insert(newMap, times, left, right)
        }
        return PairMap(newMap)
    }

    private fun insert(pairCount: MutableMap<String, Long>, times: Long, vararg items: String) {
        items.forEach {
            val currentCount = pairCount.getOrDefault(it, 0L)
            pairCount[it] = currentCount + times
        }
    }

    fun buildDictionary() = input.split("\n").associate { line ->
        val (_, from, to) = regex.find(line)!!.groupValues
        from to to
    }

    private val regex = Regex("([A-Z]{2})\\s+->\\s+([A-Z])")
    const val initialChain = "CHBBKPHCPHPOKNSNCOVB"
    private val input = """
        SP -> K
        BB -> H
        BH -> S
        BS -> H
        PN -> P
        OB -> S
        ON -> C
        HK -> K
        BN -> V
        OH -> F
        OF -> C
        SN -> N
        PF -> H
        CF -> F
        HN -> S
        SK -> F
        SS -> C
        HH -> C
        SO -> B
        FS -> P
        CB -> V
        NK -> F
        KK -> P
        VN -> H
        KF -> K
        PS -> B
        HP -> B
        NP -> P
        OO -> B
        FB -> V
        PO -> B
        CN -> O
        HC -> B
        NN -> V
        FV -> F
        BK -> K
        VC -> K
        KV -> V
        VF -> V
        FO -> O
        FK -> B
        HS -> C
        OV -> F
        PK -> F
        VV -> S
        NH -> K
        SH -> H
        VB -> H
        NF -> P
        OK -> B
        FH -> F
        CO -> V
        BC -> K
        PP -> S
        OP -> V
        VO -> C
        NC -> F
        PB -> F
        KO -> O
        BF -> C
        VS -> K
        KN -> P
        BP -> F
        KS -> V
        SB -> H
        CH -> N
        HF -> O
        CV -> P
        NB -> V
        FF -> H
        OS -> S
        CS -> S
        KC -> F
        NS -> N
        NV -> O
        SV -> V
        BO -> V
        BV -> V
        CC -> F
        CK -> H
        KP -> C
        KH -> H
        KB -> F
        PH -> P
        VP -> P
        OC -> F
        FP -> N
        HV -> P
        HB -> H
        PC -> N
        VK -> H
        HO -> V
        CP -> F
        SF -> N
        FC -> P
        NO -> K
        VH -> S
        FN -> F
        PV -> O
        SC -> N
    """.trimIndent()
}

data class PairMap(val map: MutableMap<String, Long>)