package dev.gangster.socket.handler

import dev.gangster.socket.core.Connection
import dev.gangster.socket.message.XtMessage
import dev.gangster.utils.Logger

class DefaultXtHandler: MessageHandler<XtMessage> {
    override fun match(message: XtMessage): Boolean {
        return true
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        Logger.warn { "XT handler not implemented for msg: $message" }
    }
}
