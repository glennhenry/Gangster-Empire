package dev.gangster.core.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBCosts(
    val buy: Int,
    val sell: Int,
) {
    companion object {
        fun dummy(): PBCosts {
            return PBCosts(
                buy = 100,
                sell = 5000
            )
        }
    }
}
