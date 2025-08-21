package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBLevels(
    val min: Int,
    val max: Int,
) {
    companion object {
        fun dummy(): PBLevels {
            return PBLevels(
                min = 1,
                max = 7
            )
        }
    }
}
