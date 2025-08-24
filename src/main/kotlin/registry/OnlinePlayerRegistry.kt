package dev.gangster.registry

import io.ktor.util.date.getTimeMillis
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps track online players and their status ([PlayerStatus]).
 */
class OnlinePlayerRegistry {
    private val players = ConcurrentHashMap<String, PlayerStatus>()

    /**
     * Mark the [playerId] as online. Does nothing if player is already online
     */
    fun markOnline(playerId: String) {
        if (!players.contains(playerId)) {
            players[playerId] = PlayerStatus(
                playerId = playerId,
                onlineSince = getTimeMillis(),
            )
        }
    }

    /**
     * Mark a player of [playerId] as offline. Does nothing if player is already offline
     */
    fun markOffline(playerId: String) {
        players.remove(playerId)
    }

    /**
     * Find out whether [playerId] is online.
     */
    fun isOnline(playerId: String): Boolean {
        return players.contains(playerId)
    }

    /**
     * Return the online status of [playerId].
     *
     * @return `null` if player is not online.
     */
    fun getStatus(playerId: String): PlayerStatus? {
        return players[playerId]
    }

    /**
     * List all online players
     */
    fun listOnline(): List<PlayerStatus> {
        return players.values.toList()
    }

    fun close() {
        players.clear()
    }
}
