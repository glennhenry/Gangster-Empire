package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewInventoryRequest(
    val actuallyEmpty: String? = null,
)
