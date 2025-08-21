package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentGetArmamentPresetStatusRequest(
    val actuallyEmpty: String? = null
)
