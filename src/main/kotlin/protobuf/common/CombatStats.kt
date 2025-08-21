package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class CombatStats(
    val damage: Int?,
    val health: Int?,
    val critical: Float?,
    val resistance: Float?,
    val hitChance: Float?,
) {
    companion object {
        fun dummy(): CombatStats {
            return CombatStats(
                damage = 10,
                health = 500,
                critical = 30f,
                resistance = 30f,
                hitChance = 70f
            )
        }
    }
}
