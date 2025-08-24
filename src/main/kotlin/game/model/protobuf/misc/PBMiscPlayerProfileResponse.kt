package dev.gangster.game.model.protobuf.misc

import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.data.AdminData
import kotlinx.serialization.Serializable

/**
 * result is enum where OK = 1, PLAYER_UNKNOWN = 2, INVALID_NAME = 3
 */
@Serializable
data class PBMiscPlayerProfileResponse(
    val result: Int = 1,
    val playerId: Int?,
    val playerName: String?,
    val profile: PBPlayerProfile?
) {
    companion object {
        fun dummy(): PBMiscPlayerProfileResponse {
            return PBMiscPlayerProfileResponse(
                result = 1,
                playerId = AdminData.PLAYER_ID_NUMBER,
                playerName = AdminData.USERNAME,
                profile = PBPlayerProfile.dummy()
            )
        }
    }
}
