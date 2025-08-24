package dev.gangster.auth

import dev.gangster.data.collection.model.AvatarData
import dev.gangster.db.Database
import dev.gangster.utils.Logger

class InGameAuthProvider(
    private val db: Database,
    private val playerAccountRepository: PlayerAccountRepository,
) : AuthProvider {

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        avatarData: AvatarData
    ): AuthResult {
        if (doesUserExist(username)) {
            return AuthResult.Failure(AuthFailureReason.USERNAME_TAKEN)
        }

        val result = db.createPlayer(username, email, password, avatarData)
        if (result.isFailure) {
            return AuthResult.Failure(AuthFailureReason.DATABASE_ERROR)
        }

        return AuthResult.Success(result.getOrThrow())
    }

    override suspend fun login(
        username: String,
        password: String
    ): AuthResult {
        if (!doesUserExist(username)) {
            return AuthResult.Failure(AuthFailureReason.USER_NOT_FOUND)
        }

        val result = playerAccountRepository.verifyCredentials(username, password)

        return if (result.isSuccess) {
            AuthResult.Success(result.getOrThrow())
        } else {
            val e = result.exceptionOrNull()
            when (e) {
                is NoSuchElementException -> AuthResult.Failure(AuthFailureReason.USER_NOT_FOUND)
                is IllegalArgumentException -> AuthResult.Failure(AuthFailureReason.INVALID_CREDENTIALS)
                else -> AuthResult.Failure(AuthFailureReason.DATABASE_ERROR)
            }
        }
    }

    override suspend fun doesUserExist(username: String): Boolean {
        val result = playerAccountRepository.doesUserExist(username)

        return if (result.isFailure) {
            Logger.error { "Error: InGameAuthProvider(doesUserExist): ${result.exceptionOrNull()}" }
            false
        } else {
            true
        }
    }
}
