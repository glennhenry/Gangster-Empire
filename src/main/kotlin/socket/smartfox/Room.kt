package dev.gangster.socket.smartfox

/**
 * Representation of a SmartFox room.
 *
 * We currently use single room for everyone. Configuration follows the default of live server.
 *
 * In the context of gangster, room is logical instance of server (e.g., English 1, World 1, World 2, etc).
 * Multiple rooms can exist in one server instance. In other word, room is subset of zone.
 *
 * Zone is physical instance of server (i.e., English server has different zone with German server).
 */
data class Room(
    val roomId: Int,
    val userCount: Int,
    val maxUsers: Int = 1000,
    val temporary: Boolean = true,
    val game: Boolean = false,
    val private: Boolean = false,
    val limbo: Boolean = false,
    val name: String = "Lobby",
)

/**
 * Convert flags of room into Int (in this order: `private`, `temporary`, `game`, `limbo`).
 */
fun Room.convertFlags(): Int {
    var flags = 0
    if (private)   flags = flags or 1
    if (temporary) flags = flags or 2
    if (game)      flags = flags or 4
    if (limbo)     flags = flags or 8
    return flags
}
