package dev.gangster.auth

sealed class AuthResult {
    data class Success(val playerId: Int) : AuthResult()
    data class Failure(val reason: AuthFailureReason) : AuthResult()
}

enum class AuthFailureReason {
    USERNAME_TAKEN,
    EMAIL_TAKEN,
    USER_NOT_FOUND,
    INVALID_CREDENTIALS,
    DATABASE_ERROR,
    UNKNOWN
}
