package dev.gangster.socket.handler.extension

import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.protocol.SmartFoxString

/**
 * Handle version check (again)
 */
class XtVersionCheckHandler : MessageHandler<XtMessage> {
    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_VCK
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        val buildNumberServer = 0
        val unknown = 0
        connection.sendRaw(
            SmartFoxString.makeXt("vck", message.reqId, buildNumberServer, unknown)
        )
    }
}
