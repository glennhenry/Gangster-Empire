package dev.gangster.game.model.protobuf.shop

import dev.gangster.game.model.protobuf.common.PBShopType
import kotlinx.serialization.Serializable

@Serializable
data class PBShopViewItemsRequest(
    val shop: PBShopType
)
