package dev.gangster.model.protobuf

import dev.gangster.model.protobuf.common.PBItemSlot
import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewArmamentResponse(
    val playerId: Int,
    val width: Int,
    val height: Int,
    val itemSlots: List<PBItemSlot>,
    val isUnlockable: Boolean,
    val unlockCost: Int?,
    val nextPaidUnlockLevel: Int?,
    val nextFreeUnlockLevel: Int?,
) {
    companion object {
        fun dummy(pid: Int): PBEquipmentViewArmamentResponse {
            return PBEquipmentViewArmamentResponse(
                playerId = pid,
                width = 8,
                height = 5,
                itemSlots = emptyList(),
                isUnlockable = false,
                unlockCost = null,
                nextPaidUnlockLevel = null,
                nextFreeUnlockLevel = 4
            )
        }
    }
}
