package dev.gangster.game.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewFoodRequest(
    val playerId: Int,
)
