package dev.gangster.core.model.protobuf.work

import kotlinx.serialization.Serializable

@Serializable
data class PBWorkViewWorkRequest(
    val actuallyEmpty: String? = null
)
