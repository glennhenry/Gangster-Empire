package dev.gangster.game.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LgnRequest(
    val name: String,
    val pw: String,
    val lang: String,
    val did: String,
    val connectTime: String,
    val roundTripTime: Int,
    val accountId: String,
)
