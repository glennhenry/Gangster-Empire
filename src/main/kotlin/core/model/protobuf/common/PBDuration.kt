package dev.gangster.core.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBDuration(
    val total: Int,
    val remaining: Int?
) {
    companion object {
        fun dummy(): PBDuration {
            return PBDuration(
                total = 1000,
                remaining = 450
            )
        }
    }
}
