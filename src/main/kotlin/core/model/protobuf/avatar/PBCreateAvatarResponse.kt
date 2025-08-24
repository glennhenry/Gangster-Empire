package dev.gangster.core.model.protobuf.avatar

import kotlinx.serialization.Serializable

/**
 * result ok = 1
 */
@Serializable
data class PBCreateAvatarResponse(
    val result: Int = 1
)
