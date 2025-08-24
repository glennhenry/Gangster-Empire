package dev.gangster.game.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentGetArmamentPresetStatusRequest(
    val actuallyEmpty: String? = null
)
