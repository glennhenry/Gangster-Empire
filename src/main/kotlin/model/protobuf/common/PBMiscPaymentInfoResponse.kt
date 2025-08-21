package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBMiscPaymentInfoResponse(
    val isPayUser: Boolean,
    val dopplerCount: Int, // what is this??? is it how many times player have recharged?
) {
    companion object {
        fun dummy(): PBMiscPaymentInfoResponse {
            return PBMiscPaymentInfoResponse(
                isPayUser = false,
                dopplerCount = 0
            )
        }
    }
}
