package dev.gangster.socket.protocol

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
     * Parse Xt message with protobuf payload
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

        return XtMessage(zone, command, reqId, payload)
    }
}
