package dev.gangster.socket.protocol

import dev.gangster.context.GlobalContext
import dev.gangster.socket.message.XtMessage
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

/**
 * Smart fox XT structure for protobuf: %xt%<zone>%<command>%<reqId>%<payload>%
 * Smart fox XT structure for raw message: %xt%<zone>%<command>%<reqId>%<status_code>%<args_strings>%
 */
object SmartFoxString {
    fun makeXt(type: String, vararg msg: Any?): String {
        return buildString {
            append("%xt")
            append("%$type")
            msg.forEach {
                if (it == null || it == "") {
                    append("%")
                } else {
                    append("%$it")
                }
            }
            append("%\u0000")
        }
    }

    /**
     * Parse XT message generically into string parts
     * Example: %xt%MafiaEx%lre%1%foo%bar%baz%
     */
    fun parseXt(raw: ByteArray): XtMessage {
        val s = raw.toString(Charsets.UTF_8).trimEnd('%', '\u0000')
        val parts = s.split('%').drop(2) // drop leading empty and "xt"
        require(parts.size >= 3) { "Invalid XT message: $s" }

        val zone = parts[0]
        val command = parts[1]
        val reqId = parts[2]

        // clean empty (RoundHouseKick) and drop trailing 0 (protocol)
        val params =
            parts.drop(3).map {
                if (it == "<RoundHouseKick>") "" else it
            }.dropLastWhile { it.isEmpty() || it == "0" }

        return XtMessage(zone, command, reqId, params.dropLast(1))
    }

    /**
     * Parse XT message with Protobuf payload
     * Example: %xt%MafiaEx%createavatar%1%CAIQAx...%
     */
    fun parsePbXt(raw: ByteArray): XtMessage {
        val percent = '%'.code.toByte()
        val positions = mutableListOf<Int>()
        for (i in raw.indices) {
            if (raw[i] == percent) {
                positions.add(i)
                if (positions.size == 5) break
            }
        }

        require(positions.size == 5) { "Invalid XT message" }

        fun sliceAsString(start: Int, end: Int) =
            raw.copyOfRange(start, end).toString(Charsets.UTF_8)

        val zone = sliceAsString(positions[1] + 1, positions[2])
        val command = sliceAsString(positions[2] + 1, positions[3])
        val reqId = sliceAsString(positions[3] + 1, positions[4])

        val payloadBase64 = raw.copyOfRange(positions[4] + 1, raw.size - 2) // trim the last %
            .toString(Charsets.UTF_8)

        val payload = Base64.decode(payloadBase64)

        return XtMessage(zone, command, reqId, emptyList(), payload)
    }

    /**
     * Parse XT message with JSON object payload
     * Example: %xt%MafiaEx%lre%1%{"mail":"x","pw":"y"}%
     */
    inline fun <reified T> parseJsonXt(raw: ByteArray, json: Json = GlobalContext.json): T {
        val xt = parseXt(raw)
        require(xt.stringParts.isNotEmpty()) { "XT object payload missing" }

        val objStr = xt.stringParts.first() // JSON payload is first param
        return json.decodeFromString(objStr)
    }

    /**
     * Parse XT message from string parts type and transform it into an object type
     * Example: %xt%MafiaEx%lre%1%foo%bar%baz% the payload becomes data class
     */
    inline fun <reified T> parseObjXt(
        raw: ByteArray,
        json: Json = Json { ignoreUnknownKeys = true }
    ): Pair<XtMessage, T> {
        val xt = parseXt(raw)

        // Get constructor parameter names in order
        val primaryConstructor = T::class.constructors.first()
        val fields = primaryConstructor.parameters
            .map { it.name!! }
            .filterNot { it == "seen0" || it == "serializationConstructorMarker" }

        // Take only as many params as needed to fill the data class
        val paramsForObject = xt.stringParts.take(fields.size)

        val jsonFields = fields.zip(paramsForObject)
            .mapNotNull { (field, value) ->
                if (value.isNotEmpty()) "\"$field\":\"${escapeJson(value)}\"" else null
            }

        val objStr = "{${jsonFields.joinToString(",")}}"
        val obj = json.decodeFromString<T>(objStr)

        return xt to obj
    }

    fun escapeJson(s: String): String = buildString {
        for (c in s) {
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(c)
            }
        }
    }
}
