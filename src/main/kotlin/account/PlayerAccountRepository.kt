package dev.gangster.auth

import dev.gangster.data.collection.PlayerAccount

/**
 * Repository which manage player accounts.
 */
interface PlayerAccountRepository {
    suspend fun doesUserExist(username: String): Result<Boolean>

    suspend fun getAccountByUsername(username: String): Result<PlayerAccount>

    suspend fun getAccountByPlayerId(playerId: Int): Result<PlayerAccount>

    suspend fun getPlayerIdByUsername(username: String): Result<Int>

    suspend fun updatePlayerAccount(playerId: Int, account: PlayerAccount): Result<Unit>

    suspend fun updateLastLogin(playerId: Int, lastLogin: Long): Result<Unit>

    /**
     * Return the time in days player is banned for.
     */
    suspend fun isPlayerBanned(playerId: Int): Result<Int>

    /**
     * Verify credentials of the given username and password.
     *
     * @return playerId for the corresponding username if success.
     */
    suspend fun verifyCredentials(username: String, password: String): Result<Int>
}
