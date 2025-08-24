package dev.gangster.socket.handler.smartfox

import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.SfConstants
import dev.gangster.socket.message.SfMessage
import dev.gangster.socket.protocol.SmartFoxXML

/**
 * Respond to round trip message, which we believe is initial ping.
 */
class SfRoundTripHandler : MessageHandler<SfMessage> {
    override fun match(message: SfMessage): Boolean {
        return message.type == SfConstants.TYPE_SYS && message.action == SfConstants.ACTION_ROUND_TRIP
    }

    override suspend fun handle(
        connection: Connection,
        message: SfMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        connection.sendRaw(SmartFoxXML.roundTripResponse())
    }
}
