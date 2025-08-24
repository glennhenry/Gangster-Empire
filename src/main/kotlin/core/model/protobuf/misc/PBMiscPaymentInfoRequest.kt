package dev.gangster.core.model.protobuf.misc

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPaymentInfoRequest(
    val actuallyEmpty: String? = null,
)
