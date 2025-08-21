package dev.gangster.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class MiscPlayerProfileRequest(
    val playerId: Int?,
    val playerName: String?,
    val shortInfo: Boolean
)
