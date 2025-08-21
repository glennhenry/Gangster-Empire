package dev.gangster.model.protobuf.misc

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscNewAchievementsRequest(
    val actuallyEmpty: String? = null,
)
