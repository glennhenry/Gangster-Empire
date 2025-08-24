package dev.gangster.socket.protocol

import dev.gangster.context.GlobalContext
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

/**
 * Always have trailing `\u0000` (null byte)
 *
 * Request structure:
 * - `%xt%<zone>%<command>%<reqId>%<payload>%`
 *
 * Response structure:
 * - protobuf: `%xt%<command>%<reqId>%-1%<payload>%`
 * - raw message: `%xt%<reqId>%<status_code>%<args_strings>%` (structure of args_strings depend on each message)
 */
object SmartFoxString {
    fun makeXt(
        command: String, reqId: Int,
        statusCode: Int? = null, mode: XtMode = XtMode.Nothing, vararg msg: Any?
    ): String {
        return buildString {
            append("%xt")
            append("%$command")
            append("%$reqId")
            if (statusCode != null) {
                append("%$statusCode")
            }
            if (mode == XtMode.Protobuf) {
                append("%-1")
            }
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
        val reqId = parts[2].toInt()

        return if (parts.size == 4) {
            // looks like Protobuf XT (PbXT)
            val payloadBase64 = raw.copyOfRange(3, raw.size - 2) // trim the last %
                .toString(Charsets.UTF_8)
            val payload = Base64.decode(payloadBase64)

            XtMessage(zone, command, reqId, XtMode.Protobuf, emptyList(), payload)
        } else {
            // plain XT (multiple params)
            val params = parts.drop(3)
                .map { if (it == "<RoundHouseKick>") "" else it }
                .dropLastWhile { it.isEmpty() || it == "0" }

            XtMessage(zone, command, reqId, XtMode.Nothing, params)
        }
    }

    /**
     * Parse XT message with JSON object payload
     * Example: %xt%MafiaEx%lre%1%{"mail":"x","pw":"y"}%
     */
    inline fun <reified T> parseJsonXt(xtMessage: XtMessage, json: Json = GlobalContext.json): T {
        require(xtMessage.stringParts.isNotEmpty()) { "XT object payload missing" }

        val objStr = xtMessage.stringParts.first()
        return json.decodeFromString(objStr)
    }

    /**
     * Parse XT message from string parts type and transform it into an object type
     * Example: %xt%MafiaEx%lre%1%foo%bar%baz% the payload becomes data class
     */
    inline fun <reified T> parseObjXt(
        xtMessage: XtMessage,
        json: Json = Json { ignoreUnknownKeys = true }
    ): T {
        // Get constructor parameter names in order
        val primaryConstructor = T::class.constructors.first()
        val fields = primaryConstructor.parameters
            .map { it.name!! }
            .filterNot { it == "seen0" || it == "serializationConstructorMarker" }

        // Take only as many params as needed to fill the data class
        val paramsForObject = xtMessage.stringParts.take(fields.size)

        val jsonFields = fields.zip(paramsForObject)
            .mapNotNull { (field, value) ->
                if (value.isNotEmpty()) "\"$field\":\"${escapeJson(value)}\"" else null
            }

        val objStr = "{${jsonFields.joinToString(",")}}"
        val obj = json.decodeFromString<T>(objStr)

        return obj
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
