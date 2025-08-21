package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewGearRequest(
    val playerId: Int?
)
