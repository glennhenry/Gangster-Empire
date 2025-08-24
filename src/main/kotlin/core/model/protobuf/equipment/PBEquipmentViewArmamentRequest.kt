package dev.gangster.core.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewArmamentRequest(
    val playerId: Int?,
    val armamentNumber: Int?,
)
