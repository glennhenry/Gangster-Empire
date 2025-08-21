package dev.gangster.socket.core

import dev.gangster.SERVER_HOST
import dev.gangster.SOCKET_SERVER_PORT
import dev.gangster.context.GlobalContext
import dev.gangster.protobuf.CreateAvatarRequest
import dev.gangster.protobuf.CreateAvatarResponse
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.socket.protocol.SmartFoxXML
import dev.gangster.utils.Logger
import dev.gangster.utils.UUID
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.encoding.Base64

const val POLICY_REQUEST =
    "<cross-domain-policy><allow-access-from domain='*' to-ports='7777' /></cross-domain-policy>\u0000"

class Server(
    private val host: String = SERVER_HOST,
    private val port: Int = SOCKET_SERVER_PORT,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) {
    private val clients = ConcurrentHashMap<String, Connection>() // connectionId: Connection

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
                    clients[connection.connectionId]
                    handleClient(connection)
                }
            } catch (e: Exception) {
                Logger.error { "ERROR on server: $e" }
                shutdown()
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
                        // Version check handshake (follows original smartfox)
                        data.startsWithString("<msg t='sys'><body action='verChk'") -> {
                            connection.sendRaw(POLICY_REQUEST.toByteArray()) // first request so response this first
                            connection.sendRaw(SmartFoxXML.apiOK())
                        }

                        // Handle server login (game uses extension for login response)
                        data.startsWithString("<msg t='sys'><body action='login'") -> {
                            val r = -1             // param0
                            val roomId = 1         // param1
                            val userCount = 1      // param2
                            val maxUsers = 100     // param3
                            val flags = 2          // param4 flags for room.
                            // (_loc3_ >> 1 & 1) = temp  [1 if flags=2]
                            // (_loc3_ >> 2 & 1) = game  [0 if flags=2]
                            // (_loc3_ >> 0 & 1) = priv  [0 if flags=2]
                            // (_loc3_ >> 3 & 1) = limbo [0 if flags=2]

                            val roomName = "Lobby" // param5 must be lobby
                            connection.sendRaw(
                                SmartFoxString.makeXt(
                                    "rlu", r, roomId, userCount,
                                    maxUsers, flags, roomName
                                )
                            )
                        }

                        // Handle room list
                        data.startsWithString("<msg t='sys'><body action='autoJoin'") -> {
                            connection.sendRaw(SmartFoxXML.joinOk(r = 1, pid = 0))
                        }

                        // Response to roundTrip message (likely first periodic ping)
                        data.startsWithString("<msg t='sys'><body action='roundTrip'") -> {
                            connection.sendRaw(SmartFoxXML.roundTripResponse())
                        }

                        // Response to periodic ping which is sent after the first roundTrip
                        // follows zone name in 1.xml
                        data.startsWithString("%xt%MafiaEx%pin") -> {
                            Logger.debug { "Received xt pin message" }
                        }

                        data.startsWithString("%xt%MafiaEx%vck") -> {
                            val r = 1
                            val unknown1 = 0
                            val unknown2 = 0
                            connection.sendRaw(
                                SmartFoxString.makeXt("vck", r, unknown1, unknown2)
                            )
                        }

                        data.startsWithString("%xt%MafiaEx%createavatar") -> {
                            // ex: %xt%MafiaEx%createavatar%1%CAIQAxoqMiExfjF+Mn4xfjR+Mn4wfjAhMH40fjJ+M340fjR+M34wfjZ+Mn4xfjEw%
                            val xtReq = SmartFoxString.parsePbXt(data)
                            val pbRequest = ProtoBuf.decodeFromByteArray<CreateAvatarRequest>(xtReq.payload)
                            Logger.debug { "Received createavatar request: $pbRequest" }

                            val pbResponse = CreateAvatarResponse(result = 1)
                            val xtRes = SmartFoxString.makeXt(
                                "createavatar",
                                xtReq.reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
                            )
                            connection.sendRaw(xtRes)
                        }
                    }

                    Logger.info("<------------ SOCKET MESSAGE END ------------>")
                }
            } catch (e: Exception) {
                Logger.error { "Error in socket for ${connection.socket.remoteAddress}: $e" }
            } finally {
                Logger.info { "Client ${connection.socket.remoteAddress} disconnected" }
                connection.shutdown()
            }
        }
    }

    fun shutdown() {
        Logger.info { "Server closed." }
    }
}

fun ByteArray.startsWithBytes(prefix: ByteArray): Boolean {
    if (this.size < prefix.size) return false
    for (i in prefix.indices) {
        if (this[i] != prefix[i]) return false
    }
    return true
}

fun ByteArray.startsWithString(prefix: String, charset: Charset = Charsets.UTF_8): Boolean {
    val prefixBytes = prefix.toByteArray(charset)
    if (this.size < prefixBytes.size) return false
    for (i in prefixBytes.indices) {
        if (this[i] != prefixBytes[i]) return false
    }
    return true
}
