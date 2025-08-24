package dev.gangster.core.model.protobuf.equipment

import kotlinx.serialization.Serializable

/**
 * Example request: %xt%getarmamentpresetstatus%1%-1%CAIQARjQDw==%
 */
@Serializable
data class PBEquipmentGetArmamentPresetStatusResponse(
    val unlockedArmaments: Int,
    val activeArmament: Int,
    val unlockCost: Int,
) {
    companion object {
        fun dummy(): PBEquipmentGetArmamentPresetStatusResponse {
            return PBEquipmentGetArmamentPresetStatusResponse(
                unlockedArmaments = 2,
                activeArmament = 1,
                unlockCost = 500
            )
        }
    }
}
