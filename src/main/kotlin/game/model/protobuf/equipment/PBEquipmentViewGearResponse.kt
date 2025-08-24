package dev.gangster.game.model.protobuf.equipment

import dev.gangster.game.model.protobuf.common.PBItem
import kotlinx.serialization.Serializable

/**
 * example request: %xt%viewgear%1%-1%CLDkpQISIggKEAEYASIGCKAGEOgCKAAyBAgFEBQ4AUIICAAQEhgAIAASIggtEAEYASIGCKAGEOgCKAAyBAgKEB44AkIICAoQABgAIAoSIghAEAEYASIGCKAGEOgCKAAyBAgFEBQ4A0IICAAQDBgAIAwSIghXEAMYASIGCKgZEJQfKAEyBAgKEB44BEIICA0QABgAIA0SIghqEAEYASIGCKAGEOgCKAAyBAgFEBQ4BUIICAgQCBgIIAASIwiGARABGAEiBgigBhDoAigAMgQIChAeOAZCCAgAEBcYACAAEiMImwEQARgBIgYIoAYQ6AIoADIECAUQFDgHQggIGxAAGAAgABIjCLABEAEYASIGCKAGEOgCKAAyBAgFEBQ4CEIICAAQDBgAIAw=%
 */
@Serializable
data class PBEquipmentViewGearResponse(
    val playerId: Long,
    val items: List<PBItem>
) {
    companion object {
        fun empty(pid: Long): PBEquipmentViewGearResponse {
            return PBEquipmentViewGearResponse(
                playerId = pid,
                items = emptyList()
            )
        }
    }
}
