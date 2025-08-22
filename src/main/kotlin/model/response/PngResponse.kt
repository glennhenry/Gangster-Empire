package dev.gangster.model.response

import dev.gangster.model.constants.City
import dev.gangster.model.user.MafiaClanData
import dev.gangster.model.user.MafiaPoliceData
import dev.gangster.model.user.MafiaProgressData
import dev.gangster.model.user.toPngResponsePart
import kotlinx.serialization.Serializable

/**
 * png (ping) response
 *
 * must encode [progressData] and [policeData] with `toPngResponsePart`
 *
 * example: %xt%png%1%0%0%0%0+0+0+0%0%0+0+0+0%-28421048%%
 */
@Serializable
data class PngResponse(
    val city: City,
    val progressData: MafiaProgressData,
    val messageDataAmount: Int,     // see PBMessageShowNewsMessage for actual message
    val policeData: MafiaPoliceData,
    val duelCooldown: Int,          // below or equal to 0 means no cooldown
    val clanStatus: MafiaClanData?, // no clan = null
) {
    companion object {
        fun empty(): PngResponse {
            return PngResponse(
                city = City.NewYork,
                progressData = MafiaProgressData.noMission(),
                messageDataAmount = 0,
                policeData = MafiaPoliceData.noPolice(),
                duelCooldown = 0,
                clanStatus = null
            )
        }
    }
}

fun PngResponse.toListOfResponse(): List<Any?> {
    return listOf(
        city.ordinal,
        progressData.toPngResponsePart(),
        messageDataAmount,
        policeData.toPngResponsePart(),
        duelCooldown,
        clanStatus?.toPngResponsePart()
    )
}
