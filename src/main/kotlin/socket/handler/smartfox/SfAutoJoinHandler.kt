package dev.gangster.socket.handler.smartfox

import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.SfConstants
import dev.gangster.socket.message.SfMessage
import dev.gangster.socket.protocol.SmartFoxXML
import dev.gangster.utils.startsWithString

/**
 * Handle `autoJoin` message as a replacement for `getRmList`.
 */
class SfAutoJoinHandler: MessageHandler<SfMessage> {
    override fun match(message: SfMessage): Boolean {
        return message.type == SfConstants.TYPE_SYS && message.action == SfConstants.ACTION_AUTO_JOIN
    }

    override suspend fun handle(
        connection: Connection,
        message: SfMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        // pid is not user in-game pid because user has not yet logged in to their account.
        connection.sendRaw(SmartFoxXML.joinOk(message.reqId, pid = 0))
    }
}
