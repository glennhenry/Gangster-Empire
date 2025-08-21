package dev.gangster.protobuf

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * result ok = 1
 */
@Serializable
data class CreateAvatarResponse(
    @ProtoNumber(1) val result: Int = 1
)
