package dev.gangster.game.model.protobuf.common

import dev.gangster.game.model.components.PortraitData
import dev.gangster.game.data.AdminData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * playerprofile command
 * [example: %xt%playerprofile%1%-1%CAEQsOSlAiKCAQoKSmVubnlIYXplXxACGikyITB+Mn4wfjJ+MX4yfjB+MCEwfjN+Mn4wfjR+NH4wfjJ+M34wfjJ+NiACKgoIlQIQ5gIYQSBaMhYIwg0QzMwBHRsbP0ElA7A0QS0AAMhCSg8KBQgeEPZEEgYIpDcQ3XBYtwFgTmgAcBJ4AYgB/////wc=%]
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PBPlayerProfile(
    @ProtoNumber(1) val name: String,
    @ProtoNumber(2) val gender: PBGender,
    @ProtoNumber(3) val picString: String,
    @ProtoNumber(4) val characterClass: PBCharacterClass,
    @ProtoNumber(5) val attributes: PBAttributes,
    @ProtoNumber(6) val combatStats: PBCombatStats,
    @ProtoNumber(9) val highscore: PBHighscore, // 7, 8 is skipped in client
    @ProtoNumber(11) val wonDuels: Int,
    @ProtoNumber(12) val lostDuels: Int,
    @ProtoNumber(13) val isIgnored: Boolean,
    @ProtoNumber(14) val level: Int,
    @ProtoNumber(15) val cityId: PBCity,
    @ProtoNumber(16) val clanName: String?,
    @ProtoNumber(17) val clanId: Int?,
    @ProtoNumber(18) val rankInClan: PBFamilyRank?,
) {
    companion object {
        fun dummy(): PBPlayerProfile {
            return PBPlayerProfile(
                name = AdminData.USERNAME,
                gender = AdminData.GENDER,
                picString = PortraitData.randomMale(),
                characterClass = AdminData.CHARACTER_CLASS,
                attributes = PBAttributes.hacker(),
                combatStats = PBCombatStats.dummy(),
                highscore = PBHighscore.dummy(),
                wonDuels = 0,
                lostDuels = 0,
                isIgnored = false,
                level = 1,
                cityId = PBCityConstants.NEW_YORK,
                clanName = null,
                clanId = null,
                rankInClan = null
            )
        }
    }
}
