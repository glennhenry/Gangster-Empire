package dev.gangster.core.model.protobuf.quest

import dev.gangster.core.model.protobuf.common.PBCity
import dev.gangster.core.model.protobuf.common.PBItem
import kotlinx.serialization.Serializable

@Serializable
data class PBQuestReward(
    val cash: Int?,
    val gold: Int?,
    val xp: Int?,
    val ap: Int?,
    val item: PBItem?,
    val unlockCity: PBCity?
) {
    companion object {
        fun dummy(): PBQuestReward {
            return PBQuestReward(
                cash = 12345,
                gold = 54321,
                xp = 12000,
                ap = 123,
                item = null,
                unlockCity = null
            )
        }
    }
}
