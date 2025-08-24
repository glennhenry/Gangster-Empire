package dev.gangster.core.model.vo

import kotlinx.serialization.Serializable

/**
 * id is subtracted by 1 in client-side
 * case id 0 (in server): no event
 * case id 1: MafiaClanLevelUpDialog
 * case id 2: StandardOkDialog
 * case id 3: MafiaShowClanWarDialog
 * case id 4: MafiaShowClanWarDialog
 * case id 5: StandardOkDialog
 * case id 6: MafiaShowClanWarDialog
 * case id 7: MafiaShowClanWarDialog
 * case id 8: StandardOkDialog
 *
 * for detail see MafiaClanData parseClanStatus
 */
@Serializable
data class ClanStatusVO(
    val id: Int,
    val value: String,
    val clanId: Int?,
)

fun ClanStatusVO.toResponse(): String {
    val optional = if (clanId != null) "+$clanId" else ""
    return "$id+$value$optional"
}
