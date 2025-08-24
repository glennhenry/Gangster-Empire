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
    val playerContextRegistry: PlayerContextRegistry,
    val config: ServerConfig,
)

/**
 * Get player context for the [playerId] or null if it's not found.
 */
fun ServerContext.getPlayerContextOrNull(playerId: Long): PlayerContext? =
    playerContextRegistry.getContext(playerId)

/**
 * Force get player context for the [playerId].
 *
 * @throws IllegalStateException if context is not found.
 */
fun ServerContext.requirePlayerContext(playerId: Long): PlayerContext =
    getPlayerContextOrNull(playerId)
        ?: error("PlayerContext not found for pid=$playerId")

data class ServerConfig(
    val useMongo: Boolean,
    val mongoUrl: String,
    val isProd: Boolean,
)
