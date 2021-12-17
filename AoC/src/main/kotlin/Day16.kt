import Day16.part1
import Day16.part2
import java.util.Collections.singletonList

fun main() {
    part1()
    part2()
}

object Day16 {
    fun part1() {
        val input = binaryString(problemInput)
        val parsed = parse(input)
        println("Input: $parsed")
        println("Sum of versions = ${sumVersions(parsed.packets)}")
    }

    fun part2() {
        val input = binaryString(problemInput)
        val parsed = parse(input)
        println("Input: $parsed")
        val totalValue = evaluate(parsed.packets.first())
        println("Evaluated to $totalValue")
    }

    private fun evaluate(packet: Packet): Long = when (packet) {
        is ValuePacket -> packet.value
        is OperatorPacket -> {
            val packetValues = packet.packets.map { evaluate(it) }
            when (packet.type) {
                0 -> packetValues.sum()
                1 -> packetValues.reduce { acc, value -> acc * value }
                2 -> packetValues.minOrNull()!!
                3 -> packetValues.maxOrNull()!!
                5 -> {
                    val (lhs, rhs) = packetValues
                    if (lhs > rhs) 1 else 0
                }
                6 -> {
                    val (lhs, rhs) = packetValues
                    if (lhs < rhs) 1 else 0
                }
                7 -> {
                    val (lhs, rhs) = packetValues
                    if (lhs == rhs) 1 else 0
                }
                else -> throw Error("Unexpected packet type: ${packet.type}")
            }
        }
    }


    fun sumVersions(packets: Collection<Packet>): Int {
        var totalSum = 0
        packets.forEach {
            totalSum += it.version
            if (it is OperatorPacket) totalSum += sumVersions(it.packets)
        }
        return totalSum
    }

    fun parse(input: String): ParseResult {
        val (version, type, rest) = input.splitVersionTypeAndRest()
        return if (type == 4) parseValue(version, type, rest)
        else parseOperator(version, type, rest)
    }

    fun parseValue(version: Int, type: Int, input: String): ParseResult {
        var rest = input
        var constructedValue = ""
        do {
            val hasMore = rest.take(1) == "1"
            val nextChunk = rest.drop(1).take(4)
            constructedValue += nextChunk
            rest = rest.drop(5)
        } while (hasMore)
        return ParseResult(
            singletonList(ValuePacket(version, type, constructedValue, constructedValue.toLong(2))),
            rest
        )
    }

    fun parseOperator(version: Int, type: Int, input: String): ParseResult {
        return if (input.take(1) == "0") parseFixedSize(version, type, input.drop(1))
        else parseNumberOfPackets(version, type, input.drop(1))
    }

    fun parseFixedSize(version: Int, type: Int, input: String): ParseResult {
        val length = input.take(15).toInt(2)
        var toParse = input.drop(15).take(length)
        val parsedPackets = mutableListOf<Packet>()
        do {
            val parsed = parse(toParse)
            parsedPackets.addAll(parsed.packets)
            toParse = parsed.remaining
        } while (parsed.remaining.length > 0)
        return ParseResult(
            packets = singletonList(OperatorPacket(version, type, input, parsedPackets)),
            remaining = input.drop(15 + length)
        )
    }

    fun parseNumberOfPackets(version: Int, type: Int, input: String): ParseResult {
        val numberOfPackets = input.take(11).toInt(2)
        var toParse = input.drop(11)
        val packetsParsed = mutableListOf<Packet>()
        (1..numberOfPackets).map {
            val (parsed, remaining) = parse(toParse)
            packetsParsed.addAll(parsed)
            toParse = remaining
        }
        return ParseResult(
            packets = singletonList(OperatorPacket(version, type, input, packetsParsed)),
            remaining = toParse
        )
    }

    fun binaryString(input: String) =
        input.map { char -> char.digitToInt(16) }.map { hex -> hex.toString(2).padStart(4, '0') }.joinToString("")

    fun String.splitVersionTypeAndRest() = Triple(take(3).toInt(2), drop(3).take(3).toInt(2), drop(6))

    const val number = "D2FE28"
    const val operator = "38006F45291200"
    const val operator2 = "EE00D40C823060"
    const val firstExample = "8A004A801A8002F478"
    const val secondExample = "620080001611562C8802118E34"
    const val thirdExample = "C0015000016115A2E0802F182340"
    const val fourthExample = "A0016C880162017C3686B18A3D4780"
    const val problemInput =
        "005410C99A9802DA00B43887138F72F4F652CC0159FE05E802B3A572DBBE5AA5F56F6B6A4600FCCAACEA9CE0E1002013A55389B064C0269813952F983595234002DA394615002A47E06C0125CF7B74FE00E6FC470D4C0129260B005E73FCDFC3A5B77BF2FB4E0009C27ECEF293824CC76902B3004F8017A999EC22770412BE2A1004E3DCDFA146D00020670B9C0129A8D79BB7E88926BA401BAD004892BBDEF20D253BE70C53CA5399AB648EBBAAF0BD402B95349201938264C7699C5A0592AF8001E3C09972A949AD4AE2CB3230AC37FC919801F2A7A402978002150E60BC6700043A23C618E20008644782F10C80262F005679A679BE733C3F3005BC01496F60865B39AF8A2478A04017DCBEAB32FA0055E6286D31430300AE7C7E79AE55324CA679F9002239992BC689A8D6FE084012AE73BDFE39EBF186738B33BD9FA91B14CB7785EC01CE4DCE1AE2DCFD7D23098A98411973E30052C012978F7DD089689ACD4A7A80CCEFEB9EC56880485951DB00400010D8A30CA1500021B0D625450700227A30A774B2600ACD56F981E580272AA3319ACC04C015C00AFA4616C63D4DFF289319A9DC401008650927B2232F70784AE0124D65A25FD3A34CC61A6449246986E300425AF873A00CD4401C8A90D60E8803D08A0DC673005E692B000DA85B268E4021D4E41C6802E49AB57D1ED1166AD5F47B4433005F401496867C2B3E7112C0050C20043A17C208B240087425871180C01985D07A22980273247801988803B08A2DC191006A2141289640133E80212C3D2C3F377B09900A53E00900021109623425100723DC6884D3B7CFE1D2C6036D180D053002880BC530025C00F700308096110021C00C001E44C00F001955805A62013D0400B400ED500307400949C00F92972B6BC3F47A96D21C5730047003770004323E44F8B80008441C8F51366F38F240"

    sealed class Packet(open val version: Int, open val type: Int, open val binaryContents: String)
    data class ValuePacket(
        override val version: Int, override val type: Int, override val binaryContents: String, val value: Long
    ) : Packet(version, type, binaryContents)

    data class OperatorPacket(
        override val version: Int,
        override val type: Int,
        override val binaryContents: String,
        val packets: Collection<Packet>
    ) : Packet(version, type, binaryContents)

    data class ParseResult(val packets: Collection<Packet>, val remaining: String)
}
