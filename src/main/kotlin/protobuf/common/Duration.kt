package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Duration(
    val total: Int,
    val remaining: Int?
) {
    companion object {
        fun dummy(): Duration {
            return Duration(
                total = 1000,
                remaining = 450
            )
        }
    }
}
