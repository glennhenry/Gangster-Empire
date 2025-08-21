package dev.gangster.socket.protocol

data class XtMessage(
    val zone: String,
    val command: String,
    val reqId: String,
    val payload: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XtMessage

        if (zone != other.zone) return false
        if (command != other.command) return false
        if (reqId != other.reqId) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = zone.hashCode()
        result = 31 * result + command.hashCode()
        result = 31 * result + reqId.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}
