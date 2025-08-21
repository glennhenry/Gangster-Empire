package dev.gangster.model.protobuf

import dev.gangster.model.protobuf.common.PBItem
import kotlinx.serialization.Serializable

/**
 * example request: %xt%viewgear%1%-1%CLDkpQISIggKEAEYASIGCKAGEOgCKAAyBAgFEBQ4AUIICAAQEhgAIAASIggtEAEYASIGCKAGEOgCKAAyBAgKEB44AkIICAoQABgAIAoSIghAEAEYASIGCKAGEOgCKAAyBAgFEBQ4A0IICAAQDBgAIAwSIghXEAMYASIGCKgZEJQfKAEyBAgKEB44BEIICA0QABgAIA0SIghqEAEYASIGCKAGEOgCKAAyBAgFEBQ4BUIICAgQCBgIIAASIwiGARABGAEiBgigBhDoAigAMgQIChAeOAZCCAgAEBcYACAAEiMImwEQARgBIgYIoAYQ6AIoADIECAUQFDgHQggIGxAAGAAgABIjCLABEAEYASIGCKAGEOgCKAAyBAgFEBQ4CEIICAAQDBgAIAw=%
 */
@Serializable
data class PBEquipmentViewGearResponse(
    val playerId: Int,
    val items: List<PBItem>
) {
    companion object {
        fun empty(pid: Int): PBEquipmentViewGearResponse {
            return PBEquipmentViewGearResponse(
                playerId = pid,
                items = emptyList()
            )
        }
    }
}
