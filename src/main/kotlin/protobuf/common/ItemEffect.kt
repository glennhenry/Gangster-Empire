package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class ItemEffect(
    val id: Int,
    val first: Float,
    val second: Float?
) {
    companion object {
        fun dummy(id: Int): ItemEffect {
            return ItemEffect(
                id = id,
                first = 1f,
                second = null
            )
        }
    }
}
