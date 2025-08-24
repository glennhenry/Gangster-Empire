package dev.gangster.game.model.protobuf.avatar

import kotlinx.serialization.Serializable

/**
 * result ok = 1
 */
@Serializable
data class PBCreateAvatarResponse(
    val result: Int = 1
)
