package dev.gangster.game.model.protobuf.mission

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionBoosterShowMissionBoosterRequest(
    val actuallyEmpty: String? = null
)
