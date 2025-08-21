package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Attributes(
    val attack: Int?,
    val endurance: Int?,
    val luck: Int?,
    val toughness: Int?,
) {
    companion object {
        fun dummy(): Attributes {
            return Attributes(
                attack = 20,
                endurance = 20,
                luck = 20,
                toughness = 20
            )
        }
    }
}
