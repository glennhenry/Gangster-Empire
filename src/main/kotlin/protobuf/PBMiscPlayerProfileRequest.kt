package dev.gangster.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPlayerProfileRequest(
    val playerId: Int?,
    val playerName: String?,
    val shortInfo: Boolean
)
