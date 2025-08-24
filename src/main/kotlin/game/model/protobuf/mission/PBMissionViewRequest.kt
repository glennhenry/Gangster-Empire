package dev.gangster.game.model.protobuf.mission

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionViewRequest(
    val actuallyEmpty: String? = null,
)
