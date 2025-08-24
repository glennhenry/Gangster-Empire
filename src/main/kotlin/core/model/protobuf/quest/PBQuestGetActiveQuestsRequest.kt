package dev.gangster.core.model.protobuf.quest

import kotlinx.serialization.Serializable

@Serializable
data class PBQuestGetActiveQuestsRequest(
    val actuallyEmpty: String? = null
)
