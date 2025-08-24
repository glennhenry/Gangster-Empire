package dev.gangster.db

import dev.gangster.data.collection.PlayerAccount
import dev.gangster.data.collection.PlayerData
import dev.gangster.data.collection.model.AvatarData
import dev.gangster.data.db.CollectionName

/**
 * Represent game database.
 */
interface Database {
    /**
     * Load player account using [playerId].
     */
    suspend fun loadPlayerAccount(playerId: Int): Result<PlayerAccount>

    /**
     * Load player data using [playerId].
     */
    suspend fun loadPlayerData(playerId: Int): Result<PlayerData>

    /**
     * Create player with the provided credentials and avatarData.
     *
     * @return playerId (incremented number) of the newly created player.
     */
    suspend fun createPlayer(
        username: String, email: String,
        password: String, avatarData: AvatarData
    ): Result<Int>

    /**
     * Get a particular collection without type safety.
     *
     * Typically used when repository independent of DB implementation needs
     * to its implementor collection.
     */
    suspend fun <T> getCollection(name: CollectionName): T

    /**
     * Close database. This will be called when server turns off.
     */
    suspend fun close()
}
