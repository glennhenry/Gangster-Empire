package dev.gangster.auth

import dev.gangster.data.collection.model.AvatarData

/**
 * Represent system that provides authentication mechanism
 */
interface AuthProvider {
    /**
     * Register a new account with the provided credentials and avatar.
     *
     * @return [AuthResult] type of success if account is created successfully or failure type with the reason.
     */
    suspend fun register(username: String, email: String, password: String, avatarData: AvatarData): AuthResult

    /**
     * Login with [username] and [password].
     *
     * @return [AuthResult] type of success if login is success or failure type with the reason.
     */
    suspend fun login(username: String, password: String): AuthResult

    /**
     * Check whether a user with [username] exists.
     */
    suspend fun doesUserExist(username: String): Boolean
}
