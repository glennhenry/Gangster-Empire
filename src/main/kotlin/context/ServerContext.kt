package dev.gangster.context

import dev.gangster.auth.AuthProvider
import dev.gangster.auth.PlayerAccountRepository
import dev.gangster.db.Database
import dev.gangster.registry.OnlinePlayerRegistry
import dev.gangster.registry.PlayerContextRegistry
import dev.gangster.task.ServerTaskDispatcher

data class ServerContext(
    val db: Database,
    val playerAccountRepository: PlayerAccountRepository,
    val onlinePlayerRegistry: OnlinePlayerRegistry,
    val authProvider: AuthProvider,
    val taskDispatcher: ServerTaskDispatcher,
    val playerContextTracker: PlayerContextRegistry,
    val config: ServerConfig,
)

data class ServerConfig(
    val adminEnabled: Boolean,
    val useMongo: Boolean,
    val mongoUrl: String,
    val isProd: Boolean,
)
