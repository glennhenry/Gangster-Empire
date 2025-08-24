package dev.gangster.socket.core

import dev.gangster.utils.Logger
import dev.gangster.utils.UUID
import io.ktor.network.sockets.Socket
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Smartfox convention:
 *
 * if message starts with:
 * - "<": XML
 * - "{": JSON
 * - "%": String
 */
class Connection(
    var playerId: Int = -1,
    val connectionId: String = UUID.new(),
    val socket: Socket,
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val output: ByteWriteChannel,
) {
    /**
     * Send raw unserialized message to client
     *
     * @param b raw message in bytearray
     */
    suspend fun sendRaw(b: ByteArray, logFull: Boolean = false) {
        Logger.debug(logFull = logFull) { "Sending raw: ${b.decodeToString()}" }
        output.writeFully(b)
    }

    /**
     * Send raw unserialized message to client
     *
     * @param b raw message in string
     */
    suspend fun sendRaw(message: String, logFull: Boolean = false) {
        val bytes = message.toByteArray(Charsets.UTF_8)
        Logger.debug(logFull = logFull) { "Sending raw: $message" }
        output.writeFully(bytes)
    }


    fun close() {
        scope.cancel()
        socket.close()
    }

    override fun toString(): String {
        return "[ADDR]: ${this.socket.remoteAddress} | connectionId=$connectionId"
    }
}
