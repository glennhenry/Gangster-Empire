package dev.gangster.socket.handler

import dev.gangster.socket.core.Connection
import dev.gangster.socket.message.SocketMessage

/**
 * Handler for [SocketMessage].
 */
interface MessageHandler<T : SocketMessage> {
    /**
     * Determine whether the particular [message] should be handled.
     */
    fun match(message: T): Boolean

    /**
     * Handle the [message] from [connection].
     */
    suspend fun handle(connection: Connection, message: T, send: suspend (ByteArray) -> Unit)
}
