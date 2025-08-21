package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBCombatStats(
    val damage: Int?,
    val health: Int?,
    val critical: Float?,
    val resistance: Float?,
    val hitChance: Float?,
) {
    companion object {
        fun dummy(): PBCombatStats {
            return PBCombatStats(
                damage = 10,
                health = 500,
                critical = 30f,
                resistance = 30f,
                hitChance = 70f
            )
        }
    }
}
