package dev.gangster.game.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewInventoryRequest(
    val actuallyEmpty: String? = null,
)
