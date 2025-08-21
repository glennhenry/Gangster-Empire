package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBMission(
    val id: Int,
    val cash: Int,
    val xp: Int,
    val activity: Int,
    val time: Int,
    val opponentClass: PBCharacterClass
) {
    companion object {
        fun dummy(id: Int): PBMission {
            return PBMission(
                id = id,
                cash = 1234,
                xp = 4321,
                activity = 23,
                time = 32,
                opponentClass = PBCharacterClassConstants.BULLY
            )
        }
    }
}
