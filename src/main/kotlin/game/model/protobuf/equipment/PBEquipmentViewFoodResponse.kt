package dev.gangster.game.model.protobuf.equipment

import dev.gangster.game.model.protobuf.common.PBItem
import kotlinx.serialization.Serializable

/**
 * example reqest: %xt%viewfood%1%-1%CLDkpQISFAgAEAEYASIECAAQACgAMgQIABAAEhQIABABGAEiBAgAEAAoADIECAAQABIUCAAQARgBIgQIABAAKAAyBAgAEAA=%
 */
@Serializable
data class PBEquipmentViewFoodResponse(
    val playerId: Long,
    val items: List<PBItem>
) {
    companion object {
        fun empty(pid: Long): PBEquipmentViewFoodResponse {
            return PBEquipmentViewFoodResponse(
                playerId = pid,
                items = emptyList()
            )
        }
    }
}
