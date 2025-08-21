package dev.gangster.model.protobuf.mission

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionViewRequest(
    val actuallyEmpty: String? = null,
)
