package dev.gangster.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewGearRequest(
    val playerId: Int?
)
