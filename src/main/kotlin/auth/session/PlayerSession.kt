package dev.gangster.auth.session

data class PlayerSession(
    val playerId: String,
    val token: String,
    val issuedAt: Long,
    var expiresAt: Long,
    var lifetime: Long,
)
