package dev.gangster.model.protobuf.mission

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionBoosterGetPlayerBoosterRequest(
    val actuallyEmpty: String? = null,
)
