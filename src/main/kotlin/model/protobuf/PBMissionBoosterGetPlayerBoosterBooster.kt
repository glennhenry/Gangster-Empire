package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

/**
 * booster like bicycle, taxi, jet
 */
@Serializable
data class PBMissionBoosterGetPlayerBoosterBooster(
    val boosterId: Int,
    val boostPower: Int,
    val remaining: Int,
) {
    companion object {
        fun bike(): PBMissionBoosterGetPlayerBoosterBooster {
            return PBMissionBoosterGetPlayerBoosterBooster(
                boosterId = 1,
                boostPower = 15,
                remaining = 900
            )
        }
    }
}
