package dev.gangster.model.protobuf.quest

import kotlinx.serialization.Serializable

@Serializable
data class PBQuestGetActiveQuestsRequest(
    val actuallyEmpty: String? = null
)
