package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewArmamentRequest(
    val playerId: Int?,
    val armamentNumber: Int?,
)
