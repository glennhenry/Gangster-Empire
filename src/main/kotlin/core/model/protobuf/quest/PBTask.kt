package dev.gangster.core.model.protobuf.quest

import dev.gangster.core.model.protobuf.common.PBCity
import dev.gangster.core.model.protobuf.common.PBCityConstants
import kotlinx.serialization.Serializable

@Serializable
data class PBTask(
    val taskId: Int,
    val iconId: Int,
    val requiredValue: Int,
    val currentValue: Int,
    val city: PBCity?,
    val districtNumber: Int?,
) {
    companion object {
        fun dummy(): PBTask {
            return PBTask(
                taskId = 1,
                iconId = 1,
                requiredValue = 12,
                currentValue = 6,
                city = null,
                districtNumber = null
            )
        }
    }
}
