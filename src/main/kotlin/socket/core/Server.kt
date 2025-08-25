package dev.gangster.socket.core

import dev.gangster.SERVER_HOST
import dev.gangster.SOCKET_SERVER_PORT
import dev.gangster.context.ServerContext
import dev.gangster.socket.handler.extension.*
import dev.gangster.socket.handler.smartfox.SfAutoJoinHandler
import dev.gangster.socket.handler.smartfox.SfLoginHandler
import dev.gangster.socket.handler.smartfox.SfRoundTripHandler
import dev.gangster.socket.handler.smartfox.SfVersionCheckHandler
import dev.gangster.socket.message.MessageDispatcher
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.socket.protocol.SmartFoxXML
import dev.gangster.utils.Logger
import dev.gangster.utils.UUID
import dev.gangster.utils.startsWithString
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class Server(
    private val host: String = SERVER_HOST,
    private val port: Int = SOCKET_SERVER_PORT,
    private val context: ServerContext,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) {
    private val messageDispatcher = MessageDispatcher()

    init {
        messageDispatcher.registerSf(SfVersionCheckHandler())
        messageDispatcher.registerSf(SfLoginHandler(context))
        messageDispatcher.registerSf(SfAutoJoinHandler())
        messageDispatcher.registerSf(SfRoundTripHandler())
        messageDispatcher.registerXt(XtPinHandler())
        messageDispatcher.registerXt(XtVersionCheckHandler())
        messageDispatcher.registerXt(XtCreateAvatarHandler())
        messageDispatcher.registerXt(XtLoginRegisterHandler(context))
        messageDispatcher.registerXt(XtLoginHandler(context))
        messageDispatcher.registerXt(XtAllPlayerDataHandler(context))
    }

    fun start() {
        coroutineScope.launch {
            try {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val serverSocket = aSocket(selectorManager).tcp().bind(host, port)
                Logger.info { "Socket server started at $host:$port" }

                while (true) {
                    val socket = serverSocket.accept()
                    val connection = Connection(
                        connectionId = UUID.new(),
                        socket = socket,
                        output = socket.openWriteChannel(autoFlush = true),
                    )
                    Logger.info { "New client: ${connection.socket.remoteAddress}" }
                    handleClient(connection)
                }
            } catch (e: Exception) {
                Logger.error { "ERROR on server: $e" }
                this@Server.close()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun handleClient(connection: Connection) {
        coroutineScope.launch {
            val socket = connection.socket
            val input = socket.openReadChannel()

            try {
                val buffer = ByteArray(4096)

                while (true) {
                    val bytesRead = input.readAvailable(buffer, 0, buffer.size)
                    if (bytesRead <= 0) break

                    val data = buffer.copyOfRange(0, bytesRead)
                    Logger.debug { "Received raw: ${data.decodeToString()}" }

                    when {
                        data.startsWithString("<") -> {
                            val message = SmartFoxXML.parse(data.decodeToString())
                            messageDispatcher.findSfHandler(message)
                        }

                        data.startsWithString("%") -> {
                            val message = SmartFoxString.parseXt(data)
                            messageDispatcher.findXtHandler(message)
                        }

                        data.isNotEmpty() -> {
                            Logger.warn { "Received non-empty data but neither SF or XT message." }
                        }
                    }

                    Logger.info("<------------ SOCKET MESSAGE END ------------>")
                }
            } catch (e: Exception) {
                Logger.error { "Error in socket for ${connection.socket.remoteAddress}: $e" }
                closePlayer(connection)
            } finally {
                Logger.info { "Client ${connection.socket.remoteAddress} disconnected" }
                closePlayer(connection)
            }
        }
    }

    private suspend fun closePlayer(connection: Connection) {
        context.onlinePlayerRegistry.markOffline(connection.playerId)
        context.playerAccountRepository.updateLastLogin(connection.playerId, getTimeMillis())
        context.playerContextRegistry.removePlayer(connection.playerId)
        context.taskDispatcher.stopAllTasksForPlayer(connection.playerId)
        connection.close()
    }

    fun close() {
        context.playerContextRegistry.close()
        context.onlinePlayerRegistry.close()
        context.taskDispatcher.close()
        messageDispatcher.close()
        Logger.info { "Server closed." }
    }
}
