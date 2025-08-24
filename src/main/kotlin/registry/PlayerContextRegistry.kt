package dev.gangster.registry

import com.mongodb.kotlin.client.coroutine.MongoCollection
import dev.gangster.context.PlayerContext
import dev.gangster.context.PlayerServices
import dev.gangster.data.collection.PlayerData
import dev.gangster.data.db.CollectionName
import dev.gangster.db.Database
import dev.gangster.socket.core.Connection
import dev.gangster.utils.Logger
import io.ktor.util.date.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks each active player's context.
 */
class PlayerContextRegistry {
    val players = ConcurrentHashMap<Long, PlayerContext>()

    /**
     * Create context for a player.
     */
    suspend fun createContext(
        playerId: Long,
        connection: Connection,
        db: Database,
        useMongo: Boolean
    ) {
        val playerAccount = db.loadPlayerAccount(playerId)
        if (playerAccount.isFailure) {
            Logger.error { "Error when creating context for playerId=$playerId: ${playerAccount.exceptionOrNull()}" }
        }

        val context = PlayerContext(
            playerId = playerId,
            connection = connection,
            onlineSince = getTimeMillis(),
            playerAccount = playerAccount.getOrThrow(),
            services = initializeServices(playerId, db, useMongo)
        )
        players[playerId] = context
    }

    private suspend fun initializeServices(
        playerId: Long,
        db: Database,
        useMongo: Boolean
    ): PlayerServices {
        // if (useMongo)

        val dataCollection = db.getCollection<MongoCollection<PlayerData>>(CollectionName.PLAYER_DATA_COLLECTION)

        val playerData = db.loadPlayerData(playerId)
        if (playerData.isFailure) {
            Logger.error { "Error when initializing services for playerId=$playerId: ${playerData.exceptionOrNull()}" }
        }

        return PlayerServices(
            x = 0
        )
    }

    /**
     * Get context of [playerId].
     *
     * @return null if context isn't found.
     */
    fun getContext(playerId: Long): PlayerContext? {
        return players[playerId]
    }

    /**
     * Update the context of a player with a lambda function.
     *
     * The [update] method pass the current context and expects to return the updated context.
     */
    fun updateContext(playerId: Long, update: (PlayerContext) -> PlayerContext) {
        val context = players.get(playerId) ?: return
        players[playerId] = update(context)
    }

    /**
     * Remove player to free-up memory.
     */
    fun removePlayer(playerId: Long) {
        players.remove(playerId)
    }

    fun close() {
        players.values.forEach {
            it.connection.close()
        }
        players.clear()
    }
}
