package dev.gangster.model

import kotlinx.serialization.Serializable

/**
 * OGA (achivement data)
 * [example: %xt%oga%1%0%4813360%2+9+10#6+183+250#6+149+250#5+65+100#5+627144+1011000#3+2948+3500#0+0+125#0+0+2#0+0+10#1+3+5#%]
 *
 * each achievement is separated by #. within achievement, each parameter is separated by +
 * achievement id is not included, but starts from 0
 * order is <achievementLevel> + <achievementValue> + <achievementNextValue>
 */
@Serializable
data class AchievementVO(
    val achievementId: Int,
    val achievementLevel: Int, // current level
    val achievementValue: Int, // current progress
    val achievementNextValue: Int // required progress to level up
) {
    companion object {
        fun dummy(id: Int): AchievementVO {
            return AchievementVO(
                achievementId = id,
                achievementLevel = 0,
                achievementValue = 0,
                achievementNextValue = 10, // TO-DO need original game data
            )
        }

        fun dummyAll(): List<AchievementVO> {
            return List(10) {
                AchievementVO(
                    achievementId = it,
                    achievementLevel = 0,
                    achievementValue = 0,
                    achievementNextValue = 10, // TO-DO need original game data
                )
            }
        }
    }
}

fun AchievementVO.toPayload(): String {
    return "$achievementLevel+$achievementValue+$achievementNextValue#"
}

fun List<AchievementVO>.toPayload(): String {
    return this.joinToString(separator = "") { it.toPayload() }
}
