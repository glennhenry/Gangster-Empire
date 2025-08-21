package dev.gangster.protobuf

import dev.gangster.protobuf.common.Achievement
import kotlinx.serialization.Serializable

/**
 * This is likely when player get achievement when they are offline (e.g., win duel)
 *
 * [example: %xt%newachievements%1%-1%%]
 */
@Serializable
data class MiscNewAchievementsResponse(
    val achievements: List<Achievement>
) {
    companion object {
        fun empty(): MiscNewAchievementsResponse {
            return MiscNewAchievementsResponse(
                achievements = emptyList()
            )
        }
    }
}
