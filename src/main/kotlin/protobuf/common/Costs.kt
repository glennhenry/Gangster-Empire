package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Costs(
    val buy: Int,
    val sell: Int,
) {
    companion object {
        fun dummy(): Costs {
            return Costs(
                buy = 100,
                sell = 5000
            )
        }
    }
}
