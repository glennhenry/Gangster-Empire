package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewFoodRequest(
    val playerId: Int,
)
