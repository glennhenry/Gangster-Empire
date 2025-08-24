package dev.gangster.game.model.protobuf.work

import kotlinx.serialization.Serializable

@Serializable
data class PBWorkViewWorkRequest(
    val actuallyEmpty: String? = null
)
