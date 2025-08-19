package dev.gangster

import dev.gangster.utils.Logger
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.server.application.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
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

    @OptIn(ExperimentalSerializationApi::class)
    install(ContentNegotiation) {
        json(json)
        protobuf(ProtoBuf)
    }

    // 3. Configure HTTP
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

    // 4. Configure Logging
    install(CallLogging)

    // 5. Configure API routes
    routing {
        get("/") {
            val indexFile = File("static/index.html")
            if (indexFile.exists()) {
                call.respondFile(indexFile)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/ftracking") {
            val body = call.receiveText()
            Logger.debug {  "Received f tracking: $body" }
        }

        // web assets
        staticFiles("/assets", File("static/assets"))
        staticFiles("/game", File("static/game"))

        // game related
        staticFiles("/gangster-account", File("static/gangster-account"))
        staticFiles("/gangster-data", File("static/gangster-data"))
        staticFiles("/gangster-content", File("static/gangster-content"))
        staticFiles("/ftracking", File("static/ftracking"))
    }
}

/**
 * - id = 4
 * - gameCacheBreaker = MafiaCacheBreaker.swf
 * - gameFolder = mafia
 * - subdomain = data
 *
 * Known links:
 *
 * POST
 * 	http://f.tracking.goodgamestudios.com/clienttracker.php
 *
 * cache breaker:
 * - `http://files-ak.goodgamestudios.com/games-config/country.xml` (downloaded)
 * - `http://account.goodgamestudios.com/CookieSaver.swf` (not downloaded)
 */
