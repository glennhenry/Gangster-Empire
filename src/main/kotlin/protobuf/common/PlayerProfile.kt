package dev.gangster.protobuf.common

import dev.gangster.utils.AdminData
import kotlinx.serialization.Serializable

/**
 * gender: male = 1, female = 2
 * characterClass: bully = 1, rogue = 2, tactictian = 3
 * cityId: new_york = 1, miami = 2, hong_kong = 3, city_event = 4
 * rankInClan: none = 1, leader = 2, assistant = 3, officer = 4, member = 5
 *
 * playerprofile command
 * [example: %xt%playerprofile%1%-1%CAEQsOSlAiKCAQoKSmVubnlIYXplXxACGikyITB+Mn4wfjJ+MX4yfjB+MCEwfjN+Mn4wfjR+NH4wfjJ+M34wfjJ+NiACKgoIlQIQ5gIYQSBaMhYIwg0QzMwBHRsbP0ElA7A0QS0AAMhCSg8KBQgeEPZEEgYIpDcQ3XBYtwFgTmgAcBJ4AYgB/////wc=%]
 */
@Serializable
data class PlayerProfile(
    val name: String,
    val gender: Int,
    val picString: String,
    val characterClass: Int,
    val attributes: Attributes,
    val combatStats: CombatStats,
    val highscore: Highscore,
    val wonDuels: Int,
    val lostDuels: Int,
    val isIgnored: Boolean,
    val level: Int,
    val cityId: Int,
    val clanName: String?,
    val clanId: Int?,
    val rankInClan: Int?,
) {
    companion object {
        fun dummy(): PlayerProfile {
            return PlayerProfile(
                name = AdminData.USERNAME,
                gender = AdminData.GENDER,
                picString = "",
                characterClass = AdminData.CHARACTER_CLASS,
                attributes = Attributes.dummy(),
                combatStats = CombatStats.dummy(),
                highscore = Highscore.dummy(),
                wonDuels = 0,
                lostDuels = 0,
                isIgnored = false,
                level = 1,
                cityId = AdminData.CITY_ID,
                clanName = null,
                clanId = null,
                rankInClan = null
            )
        }
    }
}
