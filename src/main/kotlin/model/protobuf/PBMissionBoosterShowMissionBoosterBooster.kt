package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionBoosterShowMissionBoosterBooster(
    val boosterId: Int,
    val boosterPower: Int,
    val duration: Int,
    val costCurrency1: Int,
    val costCurrency2: Int,
    val earnCurrency1: Int,
) {
    companion object {
        fun dummy(): PBMissionBoosterShowMissionBoosterBooster {
            return PBMissionBoosterShowMissionBoosterBooster(
                boosterId = 1,
                boosterPower = 10,
                duration = 900,
                costCurrency1 = 100,
                costCurrency2 = 200,
                earnCurrency1 = 300
            )
        }
    }
}
