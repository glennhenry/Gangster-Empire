package dev.gangster.core.model.vo

import kotlinx.serialization.Serializable

/**
 * newachievements (new achivement data)
 * [example: %xt%newachievements%1%-1%% for empty]
 *
 * each achievement is separated by #. within achievement, each parameter is separated by +
 * achievement id is not included, but starts from 0
 * order is <achievementLevel> + <achievementValue> + <achievementNextValue>
 */
@Serializable
data class NewAchievementVO(
    val achievementId: Int,
    val achievementLevel: Int, // new level
    val allAttributesBonus: Int, // attributes bonus
    val goldBonus: Int, // received gold
    val item: ItemVO, // received item
) {
    companion object {

    }
}
