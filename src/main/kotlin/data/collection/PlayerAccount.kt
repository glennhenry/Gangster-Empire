package dev.gangster.data.collection

import dev.gangster.data.collection.model.ServerMetadata
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.Serializable

@Serializable
data class PlayerAccount(
    val playerId: String, // referenced by other collections
    val username: String,
    val email: String = "",
    val hashedPassword: String,
    val createdAt: Long = getTimeMillis(),
    val lastLogin: Long = getTimeMillis(),
    val serverMetadata: ServerMetadata,
)
