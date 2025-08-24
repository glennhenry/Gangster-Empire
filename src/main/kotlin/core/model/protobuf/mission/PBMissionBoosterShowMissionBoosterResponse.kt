package dev.gangster.core.model.protobuf.mission

import kotlinx.serialization.Serializable

/**
 * for showmissionbooster
 *
 * it's like bike, taxi, jet
 *
 * example: %xt%showmissionbooster%1%-1%Cg0IARAKGA4grBsoADAACg0IAhAUGA4gzDooADAACg0IAxAeGA4gACj0AzAACg4IBBAyGA4gACjcCzCsGw==%
 */
@Serializable
data class PBMissionBoosterShowMissionBoosterResponse(
    val boosters: List<PBMissionBoosterShowMissionBoosterBooster>
) {
    companion object {
        fun empty(): PBMissionBoosterShowMissionBoosterResponse {
            return PBMissionBoosterShowMissionBoosterResponse(
                boosters = emptyList()
            )
        }
    }
}
