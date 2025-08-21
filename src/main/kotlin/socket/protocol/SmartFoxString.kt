package dev.gangster.socket.protocol

import dev.gangster.context.GlobalContext
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

/**
 * Smart fox XT structure: %xt%<zone>%<command>%<reqId>%<payload>%
 */
object SmartFoxString {
    fun makeXt(type: String, vararg msg: Any): String {
        return buildString {
            append("%xt")
            append("%$type")
            msg.forEach {
                append("%$it")
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
        val parts = s.split('%').drop(1) // drop the leading "xt"
        require(parts.size >= 3) { "Invalid XT message: $s" }

        val zone = parts[0]
        val command = parts[1]
        val reqId = parts[2]
        val params = parts.drop(3)

        return XtMessage(zone, command, reqId, stringParts = params)
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
    inline fun <reified T> parseObjXt(raw: ByteArray, json: Json = GlobalContext.json): T {
        val xt = parseXt(raw)
        require(xt.stringParts.isNotEmpty()) { "XT object payload missing" }

        val objStr = xt.stringParts.first() // JSON payload is first param
        return json.decodeFromString(objStr)
    }
}
