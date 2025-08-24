package dev.gangster.context

import dev.gangster.data.collection.PlayerAccount
import dev.gangster.socket.core.Connection

data class PlayerContext(
    val playerId: Int,
    val connection: Connection,
    val onlineSince: Long,
    val playerAccount: PlayerAccount,
    val services: PlayerServices
)

data class PlayerServices(
    val x: Int = 0
)
