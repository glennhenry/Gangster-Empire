package dev.gangster.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscNewAchievementsRequest(
    val actuallyEmpty: String? = null,
)
