package dev.gangster.core.model.protobuf.quest

import dev.gangster.core.model.protobuf.common.PBCity
import dev.gangster.core.model.protobuf.common.PBCityConstants
import kotlinx.serialization.Serializable

@Serializable
data class PBQuest(
    val questId: Int,
    val questType: PBQuestType,
    val questStatus: PBQuestStatus,
    val tasks: List<PBTask>,
    val reward: PBQuestReward?,
    val city: PBCity?,
    val districtNumber: Int?,
) {
    companion object {
        fun dummy(): PBQuest {
            return PBQuest(
                questId = 1,
                questType = PBQuestTypeConstants.CITY_QUEST,
                questStatus = PBQuestStatusConstants.ACTIVE,
                tasks = listOf(PBTask.dummy()),
                reward = PBQuestReward.dummy(),
                city = null,
                districtNumber = null
            )
        }
    }
}