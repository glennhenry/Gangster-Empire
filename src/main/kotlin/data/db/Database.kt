package dev.gangster.db

import dev.gangster.data.collection.PlayerAccount
import dev.gangster.data.collection.PlayerData
import dev.gangster.data.collection.model.AvatarData

/**
 * Represent game database.
 */
interface Database {
    /**
     * Load player account using [playerId].
     */
    suspend fun loadPlayerAccount(playerId: Long): PlayerAccount?

    /**
     * Load player data using [playerId].
     */
    suspend fun loadPlayerData(playerId: Long): PlayerData?

    /**
     * Create player with the provided credentials and avatarData.
     *
     * @return playerId (incremented number) of the newly created player.
     */
    suspend fun createPlayer(
        username: String, email: String,
        password: String, avatarData: AvatarData
    ): Long

    /**
     * Close database. This will be called when server turns off.
     */
    suspend fun close()
}
