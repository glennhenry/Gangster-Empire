package dev.gangster.data.collection

import dev.gangster.data.collection.model.ServerMetadata
import dev.gangster.game.data.AdminData
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.Serializable

@Serializable
data class PlayerAccount(
    val playerId: Int, // referenced by other collections
    val username: String,
    val email: String = "",
    val emailVerified: Boolean = false,
    val hashedPassword: String,
    val createdAt: Long = getTimeMillis(),
    val lastLogin: Long = getTimeMillis(),
    val serverMetadata: ServerMetadata,
) {
    companion object {
        fun admin(): PlayerAccount {
            return PlayerAccount(
                playerId = 1,
                username = AdminData.USERNAME,
                email = AdminData.EMAIL,
                emailVerified = false,
                hashedPassword = AdminData.HASHED_PASSWORD,
                createdAt = getTimeMillis(),
                lastLogin = getTimeMillis(),
                serverMetadata = ServerMetadata()
            )
        }
    }
}
