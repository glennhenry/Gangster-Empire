package dev.gangster.game.model.protobuf.mission

import kotlinx.serialization.Serializable

/**
 * result: ok = 1, invalid_parameter_value = 2
 * example: %xt%getplayerbooster%1%-1%CAEQsOSlAg==%
 */
@Serializable
data class PBMissionBoosterGetPlayerBoosterResponse(
    val result: Int = 1,
    val playerId: Int,
    val boosters: List<PBMissionBoosterGetPlayerBoosterBooster>
) {
    companion object {
        fun empty(pid: Int): PBMissionBoosterGetPlayerBoosterResponse {
            return PBMissionBoosterGetPlayerBoosterResponse(
                result = 1,
                playerId = pid,
                boosters = emptyList()
            )
        }
    }
}
