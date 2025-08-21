package dev.gangster.model.protobuf

import dev.gangster.model.protobuf.common.PBAchievement
import kotlinx.serialization.Serializable

/**
 * This is likely when player get achievement when they are offline (e.g., win duel)
 *
 * [example: %xt%newachievements%1%-1%%]
 */
@Serializable
data class PBMiscNewAchievementsResponse(
    val achievements: List<PBAchievement>
) {
    companion object {
        fun empty(): PBMiscNewAchievementsResponse {
            return PBMiscNewAchievementsResponse(
                achievements = emptyList()
            )
        }
    }
}
