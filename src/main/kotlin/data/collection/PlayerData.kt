package dev.gangster.data.collection

import dev.gangster.game.data.AdminData

data class PlayerData(
    val playerId: Int,
    val x: Int = 0
) {
    companion object {
        fun admin(): PlayerData {
            return PlayerData(
                playerId = AdminData.PLAYER_ID_NUMBER,
                x = 0
            )
        }
    }
}
