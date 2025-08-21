package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBShape(
    val width: Int,
    val height: Int
) {
    companion object {
        fun dummy(): PBShape {
            return PBShape(
                width = 2,
                height = 2
            )
        }
    }
}
