package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBAchievement(
    val id: Int,
    val level: Int,
    val allAttributesBonus: Int,
    val goldBonus: Int,
    val item: PBItem?
)
