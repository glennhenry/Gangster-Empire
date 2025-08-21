package dev.gangster.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class MiscNewAchievementsRequest(
    val actuallyEmpty: String? = null,
)
