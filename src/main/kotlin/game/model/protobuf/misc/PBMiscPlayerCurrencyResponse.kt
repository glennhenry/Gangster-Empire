package dev.gangster.game.model.protobuf.misc

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPlayerCurrencyResponse(
    val cash: Int,
    val gold: Int,
    val policeBadges: Int?,
) {
    companion object {
        fun dummy(): PBMiscPlayerCurrencyResponse {
            return PBMiscPlayerCurrencyResponse(
                cash = 123456789,
                gold = 123456,
                policeBadges = null
            )
        }
    }
}
