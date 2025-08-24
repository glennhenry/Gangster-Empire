package dev.gangster.socket.handler.smartfox

import dev.gangster.context.ServerContext
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.SfConstants
import dev.gangster.socket.message.SfMessage
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.socket.smartfox.convertFlags

/**
 * Handle SmartFox server room login (not to be confused with game login).
 *
 * The game uses SmartFox extension and didn't expect `logOk` after `login` request.
 * Responding `logOk` to `login` results in client requesting `getRmList`.
 * The `getRmList` is supposed to be responded with `rmList`.
 * However, there are no handlers registered for `rmList`, hence stuck.
 *
 * Solution is to respond with `rlu`, this prompts the game to request `autoJoin` instead,
 * in which a handler for `joinOk` is registered.
 */
class SfLoginHandler(private val serverContext: ServerContext) : MessageHandler<SfMessage> {
    override fun match(message: SfMessage): Boolean {
        return message.type == SfConstants.TYPE_SYS && message.action == SfConstants.ACTION_LOGIN
    }

    override suspend fun handle(
        connection: Connection,
        message: SfMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        val room = serverContext.rooms.first()
        connection.sendRaw(
            SmartFoxString.makeXt(
                XtConstants.COMMAND_RLU, message.reqId, room.roomId, room.userCount,
                room.maxUsers, room.convertFlags(), room.name
            )
        )
    }
}
