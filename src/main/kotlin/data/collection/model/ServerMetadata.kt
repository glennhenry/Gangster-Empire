package dev.gangster.data.collection.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerMetadata(
    val notes: String? = null,
    val bannedInDays: Int = 0,
    val flags: Map<String, Boolean> = emptyMap(),
    val extra: Map<String, String> = emptyMap(),
)
