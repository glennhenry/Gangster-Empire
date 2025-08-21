package dev.gangster.model.user

import dev.gangster.model.vo.AchievementVO
import kotlinx.serialization.Serializable

@Serializable
data class MafiaAchievementData(
    val achievements: List<AchievementVO>
) {
    companion object {
        fun dummy(): MafiaAchievementData {
            return MafiaAchievementData(
                achievements = emptyList()
            )
        }
    }
}
