package dev.gangster.core.model.protobuf.shop

import dev.gangster.core.model.protobuf.common.PBShopType
import kotlinx.serialization.Serializable

@Serializable
data class PBShopViewItemsRequest(
    val shop: PBShopType
)
