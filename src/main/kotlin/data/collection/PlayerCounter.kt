package dev.gangster.data.collection

import kotlinx.serialization.Serializable

/**
 * Store the count of registered player, to generate `playerId`.
 */
@Serializable
data class PlayerCounter(
    val _id: String = "playercounter",
    val seq: Long
)
