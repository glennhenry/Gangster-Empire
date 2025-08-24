package dev.gangster.core.model.user

import dev.gangster.core.model.vo.ClanStatusVO
import dev.gangster.core.model.vo.toResponse
import kotlinx.serialization.Serializable

/**
 * format: <clan_status_vo_1>#<clan_status_vo_n>#<end_marker>
 *         <clan_status_vo> = <id>+<value>+<clan_id?>
 *   e.g.: 1+2+3#3+2+1#1+2+3
 */
@Serializable
data class MafiaClanData(
    val clanStatus: List<ClanStatusVO>,
)

fun MafiaClanData.toPngResponsePart(): String {
    val clans = clanStatus.joinToString("#") { it.toResponse() }
    return "$clans#" // client pop the last so keep trailing #
}
