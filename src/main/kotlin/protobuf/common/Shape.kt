package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Shape(
    val width: Int,
    val height: Int
) {
    companion object {
        fun dummy(): Shape {
            return Shape(
                width = 2,
                height = 2
            )
        }
    }
}
