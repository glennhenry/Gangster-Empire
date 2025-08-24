package dev.gangster.data.collection

import dev.gangster.game.data.AdminData
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarRequest
import dev.gangster.game.model.user.MafiaUserData

data class PlayerData(
    val playerId: Int,
    val avatarData: PBCreateAvatarRequest,
    val mafiaUserData: MafiaUserData,
) {
    companion object {
        fun admin(): PlayerData {
            return PlayerData(
                playerId = AdminData.PLAYER_ID_NUMBER,
                avatarData = PBCreateAvatarRequest.dummy(),
                mafiaUserData = MafiaUserData.dummy()
            )
        }
    }
}
