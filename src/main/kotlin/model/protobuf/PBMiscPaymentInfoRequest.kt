package dev.gangster.model.protobuf

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPaymentInfoRequest(
    val actuallyEmpty: String? = null,
)
