package dev.gangster.model.protobuf.common

import dev.gangster.model.constants.ItemQuality
import dev.gangster.model.constants.ItemType
import kotlinx.serialization.Serializable

@Serializable
data class PBItem(
    val id: Int,
    val quality: ItemQuality,
    val type: ItemType,
    val costs: PBCosts,
    val isGoldPrice: Boolean,
    val levels: PBLevels,
    val subType: Int?,
    val attributes: PBAttributes?,
    val damage: PBDamage?,
    val shape: PBShape?,
    val duration: PBDuration?,
    val amount: Int?,
    val rounds: Int?,
    val effects: List<PBItemEffect>,
    val dependencies: List<PBWeaponType>,
    val charge: Int?,
) {
    companion object {
        fun dummyFood(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = ItemQuality.Normal,
                type = ItemType.Food,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = null,
                damage = null,
                shape = null,
                duration = PBDuration.dummy(),
                amount = 5,
                rounds = null,
                effects = listOf(PBItemEffect.dummy(id = 1)),
                dependencies = emptyList(),
                charge = null
            )
        }

        fun dummyWeapon(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = ItemQuality.Normal,
                type = ItemType.Weapon,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = null,
                damage = PBDamage.dummy(),
                shape = PBShape.dummy(),
                duration = null,
                amount = 1,
                rounds = null,
                effects = emptyList(),
                dependencies = emptyList(),
                charge = null
            )
        }

        fun dummyAmmo(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = ItemQuality.Normal,
                type = ItemType.Consumable,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = null,
                damage = null,
                shape = PBShape.dummy(),
                duration = null,
                amount = 1,
                rounds = 140,
                effects = emptyList(),
                dependencies = listOf(PBWeaponTypeConstants.PISTOL),
                charge = null
            )
        }

        fun dummyGear(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = ItemQuality.Normal,
                type = ItemType.Gear,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = PBAttributes.dummy(),
                damage = null,
                shape = null,
                duration = null,
                amount = 1,
                rounds = null,
                effects = emptyList(),
                dependencies = emptyList(),
                charge = null
            )
        }
    }
}
