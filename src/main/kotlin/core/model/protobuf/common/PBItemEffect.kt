package dev.gangster.core.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBItemEffect(
    val id: Int,
    val first: Float,
    val second: Float?
) {
    companion object {
        fun dummy(id: Int): PBItemEffect {
            return PBItemEffect(
                id = id,
                first = 1f,
                second = null
            )
        }
    }
}
