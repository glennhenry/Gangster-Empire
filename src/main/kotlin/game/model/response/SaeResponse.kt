package dev.gangster.game.model.response

import dev.gangster.game.model.components.MafiaSpecialEvent
import dev.gangster.game.model.components.toResponse
import kotlinx.serialization.Serializable

/**
 * Sae or special event data
 *
 * example:  %xt%sae%1%0%% (no event)
 *
 * eventId:
 * case 0 : no event
 * case 20: sicilian
 * case 80: capo vito
 * case 10: night of horror
 * case 30: calendar
 * case 40: tournament
 * case 50: breaking the law
 * case 60: lucky devil
 * case 70: eddy frost
 */
@Serializable
data class SaeResponse(
    val eventId: Int = 0,
    val event: MafiaSpecialEvent?
) {
    companion object {
        fun noEvent(): SaeResponse {
            return SaeResponse(
                eventId = 0,
                // in client its skipped if eventId = 0
                event = null
            )
        }
    }
}

fun SaeResponse.toResponse(): List<Any?> {
    return listOf(
        eventId.toString(),
        event?.toResponse()
    )
}
