package dev.gangster.game.model.user

import kotlinx.serialization.Serializable

@Serializable
data class MafiaPoliceData(
    val activeEffectTimeLeft: Int,
    val activeEffectValue: Int,
    val activeEffectBailCost: Int,
) {
    companion object {
        fun noPolice(): MafiaPoliceData {
            return MafiaPoliceData(
                activeEffectTimeLeft = 0,
                activeEffectValue = 0,
                activeEffectBailCost = 0
            )
        }
    }
}

fun MafiaPoliceData.toPngResponsePart(): String {
    return "$activeEffectTimeLeft+$activeEffectValue+$activeEffectBailCost"
}
