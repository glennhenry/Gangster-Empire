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

        fun hacker(): PBAttributes {
            return PBAttributes(
                attack = 8885555,
                endurance = 7777555,
                luck = 20,
                toughness = 20
            )
        }
    }
}
