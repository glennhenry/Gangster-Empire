package dev.gangster.model.vo

import dev.gangster.model.constants.ItemLocation
import dev.gangster.model.constants.ItemQuality
import dev.gangster.model.constants.ItemSubtype
import dev.gangster.model.constants.ItemType
import kotlinx.serialization.Serializable

@Serializable
data class ItemVO(
    val id: Int = 0,
    val type: ItemType = ItemType.None,
    val subtype: ItemSubtype = ItemSubtype.None,
    val quality: ItemQuality = ItemQuality.Normal,
    val minUnlockLevel: Int,
    val maxUnlockLevel: Int,
    val location: ItemLocation = ItemLocation.None,
    val slotId: Int = 0,
    val cash: Int = 0, //number
    val gold: Int = 0, //number
    val saleCash: Int = 0, //number
    val name: String,
    val categoryName: String,
    val categoryNameWithoutQuality: String,
    val subcategoryName: String,
    val qualityName: String,
    val rounds: UInt = 0u,
)
