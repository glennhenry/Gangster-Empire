package dev.gangster

import dev.gangster.api.apiRoutes
import dev.gangster.api.fileRoutes
import dev.gangster.context.GlobalContext
import dev.gangster.socket.core.Server
import dev.gangster.utils.LogLevel
import dev.gangster.utils.Logger
import dev.gangster.utils.decodedUrl
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

const val SERVER_HOST = "127.0.0.1"
const val FILE_SERVER_HOST = "127.0.0.1:8080"
const val API_SERVER_HOST = "127.0.0.1:8080"
const val SOCKET_SERVER_HOST = "127.0.0.1:7777"

const val FILE_SERVER_PORT = 8080
const val API_SERVER_PORT = 8080
const val SOCKET_SERVER_PORT = 7777

@OptIn(ExperimentalSerializationApi::class)
fun Application.module() {
    // 1. Configure Websockets
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        masking = true
    }

    // 2. Configure Serialization
    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val protobuf = ProtoBuf { encodeDefaults = true }

    @OptIn(ExperimentalSerializationApi::class)
    install(ContentNegotiation) {
        json(json)
        protobuf(protobuf)
    }

    // 3. Init global context
    GlobalContext.init(json, protobuf)

    // 4. Configure HTTP
    install(CORS) {
        allowHost(API_SERVER_HOST, schemes = listOf("http"))
        allowHost(SOCKET_SERVER_HOST, schemes = listOf("http"))
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    // 5. Configure Logging
    install(CallLogging)
    Logger.level = LogLevel.DEBUG // LogLevel.NOTHING disables logging

    // 6. Configure API routes
    routing {
        fileRoutes()
        apiRoutes()
    }

    // 7. Start game server
    val server = Server()
    server.start()
    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
    })
}
