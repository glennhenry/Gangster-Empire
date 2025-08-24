package dev.gangster

import com.mongodb.kotlin.client.coroutine.MongoClient
import dev.gangster.api.apiRoutes
import dev.gangster.api.fileRoutes
import dev.gangster.auth.InGameAuthProvider
import dev.gangster.auth.PlayerAccountRepository
import dev.gangster.auth.PlayerAccountRepositoryMongo
import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerConfig
import dev.gangster.context.ServerContext
import dev.gangster.data.db.CollectionName
import dev.gangster.db.Database
import dev.gangster.db.MongoDB
import dev.gangster.registry.OnlinePlayerRegistry
import dev.gangster.registry.PlayerContextRegistry
import dev.gangster.socket.core.Server
import dev.gangster.socket.smartfox.Room
import dev.gangster.task.ServerTaskDispatcher
import dev.gangster.utils.LogLevel
import dev.gangster.utils.Logger
import dev.gangster.utils.decodedUrl
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.EngineMain
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
import org.bson.Document
import java.io.File
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    EngineMain.main(args)
}

const val SERVER_HOST = "127.0.0.1"
const val FILE_SERVER_HOST = "127.0.0.1:8080"
const val API_SERVER_HOST = "127.0.0.1:8080"
const val SOCKET_SERVER_HOST = "127.0.0.1:7777"

const val FILE_SERVER_PORT = 8080
const val API_SERVER_PORT = 8080
const val SOCKET_SERVER_PORT = 7777

const val MONGO_DATABASE_NAME = "gangster"

/**
 * Setup the server:
 *
 * 1. Install Ktor modules and configure them.
 * 2. Initialize contexts: [GlobalContext], [ServerContext].
 * 3. Initialize each [ServerContext] components.
 * 4. Inject dependency.
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun Application.module() {
    // 1. Configure Serialization
    val json = Json {
        classDiscriminator = "_t"
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val protobuf = ProtoBuf { encodeDefaults = true }

    install(ContentNegotiation) {
        json(json)
        protobuf(ProtoBuf)
    }

    // 2. Initialize GlobalContext
    GlobalContext.init(
        json = json,
        pb = protobuf
    )

    // 3. Create ServerConfig
    val config = ServerConfig(
        useMongo = true,
        mongoUrl = environment.config.propertyOrNull("mongo.url")?.getString() ?: "",
        isProd = developmentMode,
    )

    // 4. Configure Database
    Logger.info { "Configuring database..." }

    lateinit var database: Database
    try {
        val mongoc = MongoClient.create(config.mongoUrl)
        val db = mongoc.getDatabase("admin")
        val commandResult = db.runCommand(Document("ping", 1))
        Logger.info { "MongoDB connection successful: $commandResult" }
        database = MongoDB(mongoc.getDatabase(MONGO_DATABASE_NAME))
    } catch (e: Exception) {
        Logger.error { "MongoDB connection failed inside timeout: ${e.message}" }
    }

    // 5. Initialize ServerContext components
    val playerAccountRepository: PlayerAccountRepository = if (config.useMongo) {
        PlayerAccountRepositoryMongo(
            accounts = database.getCollection(CollectionName.PLAYER_ACCOUNT_COLLECTION)
        )
    } else {
        // substitute with something else
        PlayerAccountRepositoryMongo(
            accounts = database.getCollection(CollectionName.PLAYER_ACCOUNT_COLLECTION)
        )
    }
    val onlinePlayerRegistry = OnlinePlayerRegistry()
    val authProvider = InGameAuthProvider(database, playerAccountRepository)
    val taskDispatcher = ServerTaskDispatcher()
    val playerContextRegistry = PlayerContextRegistry()

    // 6. Create ServerContext
    val serverContext = ServerContext(
        db = database,
        playerAccountRepository = playerAccountRepository,
        onlinePlayerRegistry = onlinePlayerRegistry,
        authProvider = authProvider,
        taskDispatcher = taskDispatcher,
        playerContextRegistry = playerContextRegistry,
        rooms = listOf(Room(
            roomId = 1,
            userCount = 0
        )),
        config = config,
    )

    // 7. Configure HTTP
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

    // 8. Configure Logging
    install(CallLogging)
    Logger.level = LogLevel.DEBUG // use LogLevel.NOTHING to disable logging
    Logger.init { logMessage ->
        // no logging to ws right now
    }

    // 10. Configure API routes
    routing {
        fileRoutes()
        apiRoutes()
    }

    // 11. Start the game socket server
    val server = Server(context = serverContext)
    server.start()
    Runtime.getRuntime().addShutdownHook(Thread {
        server.close()
    })
}
