package dev.gangster.core.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBDamage(
    val min: Int,
    val max: Int
) {
    companion object {
        fun dummy(): PBDamage {
            return PBDamage(
                min = 10,
                max = 30,
            )
        }
    }
}
