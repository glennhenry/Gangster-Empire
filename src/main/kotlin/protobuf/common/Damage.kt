package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Damage(
    val min: Int,
    val max: Int
) {
    companion object {
        fun dummy(): Damage {
            return Damage(
                min = 10,
                max = 30,
            )
        }
    }
}
