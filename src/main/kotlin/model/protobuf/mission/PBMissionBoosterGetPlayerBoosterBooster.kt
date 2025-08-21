package dev.gangster.model.protobuf.mission

import kotlinx.serialization.Serializable

/**
 * for getplayerbooster
 *
 * still confused, its similar to PBMissionBoosterShowMissionBoosterResponse
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
