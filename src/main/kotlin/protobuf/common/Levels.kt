package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Levels(
    val min: Int,
    val max: Int,
) {
    companion object {
        fun dummy(): Levels {
            return Levels(
                min = 1,
                max = 7
            )
        }
    }
}
