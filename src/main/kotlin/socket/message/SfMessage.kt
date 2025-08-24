package dev.gangster.socket.message

/**
 * Data model of SmartFox XML message.
 *
 * @property body XML part that needs to be parsed further.
 */
data class SfMessage(
    val type: String,
    val action: String,
    val reqId: Int,
    val body: String,
): SocketMessage
