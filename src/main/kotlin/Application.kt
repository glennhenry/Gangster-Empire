package dev.gangster

import dev.gangster.socket.core.Server
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
            Logger.debug { "Received f tracking: $body" }
            call.respond(HttpStatusCode.OK)
        }

        post("/logging") {
            val body = call.receiveText().decodedUrl()
            Logger.debug(logFull = true) { "Received external logging: $body" }
            Logger.debug(logFull = true) { "TL;DR; ${body.substringAfter("logMessage").substringBefore("errorCode")}" }
            call.respond(HttpStatusCode.OK)
        }

        // web assets
        staticFiles("/assets", File("static/assets"))
        staticFiles("/game", File("static/game"))

        // crossdomain
        staticFiles("crossdomain.xml", File("static"))

        // subdomain
        staticFiles("/files-ak", File("static/gangster-files"))
        staticFiles("/gangster-files", File("static/gangster-files"))
        staticFiles("/account", File("static/gangster-account"))
        staticFiles("/gangster-account", File("static/gangster-account"))
        staticFiles("/data", File("static/gangster-data"))
        staticFiles("/gangster-data", File("static/gangster-data"))
        staticFiles("/content", File("static/gangster-content"))
        staticFiles("/gangster-content", File("static/gangster-content"))

        // not yet requested, only seen in decompiled
        staticFiles("/cdn", File("static/cdn")) // CookieSaver.swf
    }

    // 6. Start game server
    val server = Server()
    server.start()
    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
    })
}

/**
 *"C:\Users\USER\.airsdk\AIRSDK_51.2.2.3\bin\adl.exe"
 * log:
 *    import com.goodgamestudios.externalLogging.ExternalLog;
 *
 *    ExternalLog.slog("message", "source");
 *
 * game save: C:\Users\<username>\AppData\Roaming\com.goodgamestudios.mafia.MafiaFrameOne\Local Store\#SharedObjects
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
 *
 *
 * SUB_DOMAIN_FILE_SERVER_OLD = "files-ak"
 * SUB_DOMAIN_MEDIA_SERVER_OLD = "media"
 * SUB_DOMAIN_DATA_SERVER_OLD = "data"
 * SUB_DOMAIN_CONTENT_SERVER_OLD = "content"
 * SUB_DOMAIN_ACCOUNT_SERVER_OLD = "account"
 * SUB_DOMAIN_FILE_SERVER_NEW = "gangster-files-ak"
 * SUB_DOMAIN_MEDIA_SERVER_NEW = "gangster-media"
 * SUB_DOMAIN_DATA_SERVER_NEW = "gangster-data"
 * SUB_DOMAIN_CONTENT_SERVER_NEW = "gangster-content"
 * SUB_DOMAIN_ACCOUNT_SERVER_NEW = "gangster-account"
 * cdnSubDomain = "content"
 */
