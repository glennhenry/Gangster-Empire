package dev.gangster.context

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
object GlobalContext {
    lateinit var json: Json
        private set
    lateinit var pb: ProtoBuf
        private set

    fun init(json: Json, pb: ProtoBuf) {
        this.json = json
        this.pb = pb
    }
}
