package dev.gangster.model.vo

import kotlinx.serialization.Serializable

@Serializable
data class FeatureVO(
    val id: Int,
    val subId: Int,
    val featureTime: String,
)

fun FeatureVO.toResponse(): String {
    return "$id+$subId+$featureTime"
}
