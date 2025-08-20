package dev.gangster

import dev.gangster.utils.Logger
import dev.gangster.utils.UUID
import io.ktor.network.sockets.Socket
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class Connection(
    var playerId: String = "",
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

    fun shutdown() {
        scope.cancel()
        socket.close()
    }

    override fun toString(): String {
        return "[ADDR]: ${this.socket.remoteAddress} | connectionId=$connectionId"
    }
}
