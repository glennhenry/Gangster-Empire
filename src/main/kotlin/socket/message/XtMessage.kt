package dev.gangster.socket.message

data class XtMessage(
    val zone: String?,
    val command: String,
    val reqId: String,
    val mode: XtMode,
    val stringParts: List<String> = emptyList(),
    val pbPayload: ByteArray = byteArrayOf(0),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XtMessage

        if (zone != other.zone) return false
        if (command != other.command) return false
        if (reqId != other.reqId) return false
        if (!pbPayload.contentEquals(other.pbPayload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = zone.hashCode()
        result = 31 * result + command.hashCode()
        result = 31 * result + reqId.hashCode()
        result = 31 * result + pbPayload.contentHashCode()
        return result
    }
}

sealed class XtMode(val value: Int) {
    object Protobuf: XtMode(-1)
    object Nothing: XtMode(0)
}
