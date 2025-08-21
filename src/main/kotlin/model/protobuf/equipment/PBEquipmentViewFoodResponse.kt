package dev.gangster.model.protobuf.equipment

import dev.gangster.model.protobuf.common.PBItem
import kotlinx.serialization.Serializable

/**
 * example reqest: %xt%viewfood%1%-1%CLDkpQISFAgAEAEYASIECAAQACgAMgQIABAAEhQIABABGAEiBAgAEAAoADIECAAQABIUCAAQARgBIgQIABAAKAAyBAgAEAA=%
 */
@Serializable
data class PBEquipmentViewFoodResponse(
    val playerId: Int,
    val items: List<PBItem>
) {
    companion object {
        fun empty(pid: Int): PBEquipmentViewFoodResponse {
            return PBEquipmentViewFoodResponse(
                playerId = pid,
                items = emptyList()
            )
        }
    }
}
