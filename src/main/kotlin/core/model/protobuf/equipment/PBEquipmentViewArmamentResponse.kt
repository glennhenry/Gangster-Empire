package dev.gangster.core.model.protobuf.equipment

import dev.gangster.core.model.protobuf.common.PBItemSlot
import kotlinx.serialization.Serializable

/**
 * example request: %xt%viewarmament%1%-1%CLDkpQIQCBgFInUIABAAGAEibQgxEAQYAyIHCOKcAhCVSSgBMgUIABCPTjgDSgUIURCYAVIECAQQAnIMCCMVcT2qPx0AAAAAcgwIJBWamRm+HQrXIzxyDAgkFZqZGT4dj8L1PHIMCBUVj8L1PB0AAAAAcgwIBxWPwvU8HQAAAAAiMwgFEAAYASIrCAUQARgFIgYIoB8QiA4oADIFCAgQj05SBAgDEANyDAgFFc3MzD0dAAAAACIzCAAQAhgBIisIBxABGAYiBgigHxCIDigAMgUICBCPTlIECAMQA3IMCAUVCtejPR0AAAAAIjMIBRADGAEiKwgCEAEYBiIGCNgEEJAcKAEyBQgAEI9OUgQIAxACcgwICRWPwvU9HQAAAAAoADgTQBY=%
 */
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
