package dev.gangster.protobuf

import dev.gangster.protobuf.common.PlayerProfile
import dev.gangster.utils.AdminData
import kotlinx.serialization.Serializable

/**
 * result is enum where OK = 1, PLAYER_UNKNOWN = 2, INVALID_NAME = 3
 */
@Serializable
data class MiscPlayerProfileResponse(
    val result: Int = 1,
    val playerId: Int?,
    val playerName: String?,
    val profile: PlayerProfile?
) {
    companion object {
        fun dummy(): MiscPlayerProfileResponse {
            return MiscPlayerProfileResponse(
                result = 1,
                playerId = AdminData.PLAYER_ID_INT,
                playerName = AdminData.USERNAME,
                profile = PlayerProfile.dummy()
            )
        }
    }
}
