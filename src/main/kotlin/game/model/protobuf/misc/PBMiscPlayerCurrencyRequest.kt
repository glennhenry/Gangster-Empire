package dev.gangster.game.model.protobuf.misc

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPlayerCurrencyRequest(
    val actuallyEmpty: String? = null,
)
