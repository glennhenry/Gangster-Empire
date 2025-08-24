package dev.gangster.game.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBItemSlot(
    val slotX: Int,
    val slotY: Int,
    val active: Boolean?,
    val item: PBItem?
) {
    companion object {
        fun dummy(): PBItemSlot {
            return PBItemSlot(
                slotX = 0,
                slotY = 0,
                active = null,
                item = null
            )
        }
    }
}
