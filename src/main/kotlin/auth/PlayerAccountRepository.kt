package dev.gangster.auth

import dev.gangster.data.collection.PlayerAccount

/**
 * Repository which manage player accounts.
 */
interface PlayerAccountRepository {
    suspend fun doesUserExist(username: String): Result<Boolean>

    suspend fun getAccountByUsername(username: String): Result<PlayerAccount>

    suspend fun getAccountByPlayerId(playerId: Long): Result<PlayerAccount>

    suspend fun getPlayerIdByUsername(username: String): Result<Long>

    suspend fun updatePlayerAccount(playerId: Long, account: PlayerAccount): Result<Unit>

    suspend fun updateLastLogin(playerId: Long, lastLogin: Long): Result<Unit>

    /**
     * Verify credentials of the given username and password.
     *
     * @return playerId for the corresponding username if success.
     */
    suspend fun verifyCredentials(username: String, password: String): Result<Long>
}
