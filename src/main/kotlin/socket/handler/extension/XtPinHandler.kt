package dev.gangster.socket.handler.extension

import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.utils.Logger

/**
 * Handle to "pin" or game's periodic ping message.
 *
 * Client seems to ignore it.
 */
class XtPinHandler : MessageHandler<XtMessage> {
    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_PIN
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        Logger.debug { "Received xt pin message, ignoring." }
    }
}
