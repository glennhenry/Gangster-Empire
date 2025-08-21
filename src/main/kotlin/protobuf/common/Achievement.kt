package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    val id: Int,
    val level: Int,
    val allAttributesBonus: Int,
    val goldBonus: Int,
    val item: Item?
)
