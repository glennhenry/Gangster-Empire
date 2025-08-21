package dev.gangster.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentGetArmamentPresetStatusRequest(
    val actuallyEmpty: String? = null
)
