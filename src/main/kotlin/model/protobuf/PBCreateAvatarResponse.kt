package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

/**
 * result ok = 1
 */
@Serializable
data class PBCreateAvatarResponse(
    val result: Int = 1
)
