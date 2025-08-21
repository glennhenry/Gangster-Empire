package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionViewRequest(
    val actuallyEmpty: String? = null,
)
