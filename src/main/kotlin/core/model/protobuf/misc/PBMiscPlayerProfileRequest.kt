package dev.gangster.core.model.protobuf.misc

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPlayerProfileRequest(
    val playerId: Int?,
    val playerName: String?,
    val shortInfo: Boolean
)
