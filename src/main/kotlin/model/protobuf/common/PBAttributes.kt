package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBAttributes(
    val attack: Int?,
    val endurance: Int?,
    val luck: Int?,
    val toughness: Int?,
) {
    companion object {
        fun dummy(): PBAttributes {
            return PBAttributes(
                attack = 20,
                endurance = 20,
                luck = 20,
                toughness = 20
            )
        }
    }
}
