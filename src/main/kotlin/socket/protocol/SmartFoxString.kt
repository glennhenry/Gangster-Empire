package dev.gangster.socket.protocol

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

        val zone = sliceAsString(positions[0] + 1, positions[1])
        val command = sliceAsString(positions[1] + 1, positions[2])
        val reqId = sliceAsString(positions[2] + 1, positions[3])

        val payload = raw.copyOfRange(positions[3] + 1, raw.size - 1) // strip trailing %

        return XtMessage(zone, command, reqId, payload)
    }
}
