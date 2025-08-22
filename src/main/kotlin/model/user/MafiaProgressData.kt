package dev.gangster.model.user

import kotlinx.serialization.Serializable

@Serializable
data class MafiaProgressData(
    val progress: Int,
    val missionTime: Int,  // unsure
    val missionGiverId: Int,
    val actionId: Int,
) {
    companion object {
        fun noMission(): MafiaProgressData {
            return MafiaProgressData(
                progress = 0,
                missionTime = 0,
                missionGiverId = 0,
                actionId = 0
            )
        }
    }
}

fun MafiaProgressData.toPngResponsePart(): String {
    return "$progress+$missionTime+$missionGiverId+$actionId"
}
