package dev.gangster.model.protobuf

import dev.gangster.model.protobuf.common.PBShopType
import kotlinx.serialization.Serializable

@Serializable
data class PBShopViewItemsRequest(
    val shop: PBShopType
)
