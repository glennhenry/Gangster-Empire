package dev.gangster.protobuf.common

import dev.gangster.model.constants.ItemQuality
import dev.gangster.model.constants.ItemType
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int,
    val quality: ItemQuality,
    val type: ItemType,
    val costs: Costs,
    val isGoldPrice: Boolean,
    val levels: Levels,
    val subType: Int?,
    val attributes: Attributes?,
    val damage: Damage?,
    val shape: Shape?,
    val duration: Duration?,
    val amount: Int?,
    val rounds: Int?,
    val effects: ItemEffect,
    val dependencies: List<WeaponType>,
    val charge: Int?,
) {
    companion object {
        fun dummyFood(id: Int): Item {
            return Item(
                id = id,
                quality = ItemQuality.Normal,
                type = ItemType.Food,
                costs = Costs.dummy(),
                isGoldPrice = false,
                levels = Levels.dummy(),
                subType = 1,
                attributes = null,
                damage = null,
                shape = null,
                duration = Duration.dummy(),
                amount = 5,
                rounds = null,
                effects = ItemEffect.dummy(id = 1),
                dependencies = emptyList(),
                charge = null
            )
        }
    }
}
