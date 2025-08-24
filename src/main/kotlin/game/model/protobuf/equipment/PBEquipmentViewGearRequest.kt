package dev.gangster.game.model.protobuf.equipment

import kotlinx.serialization.Serializable

@Serializable
data class PBEquipmentViewGearRequest(
    val playerId: Int?
)
