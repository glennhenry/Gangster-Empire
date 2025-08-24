package dev.gangster.core.model.components

import dev.gangster.utils.toInt
import kotlinx.serialization.Serializable

/**
 * Mafia special event
 *
 * The detail for each event depends on implementation
 */
@Serializable
data class MafiaSpecialEvent(
    val isEventActive: Boolean,
    val timeLeft: Int,          // in seconds
    val teaserReceived: Boolean,
) {
    companion object {
        fun eventWhichHasEnded(): MafiaSpecialEvent {
            return MafiaSpecialEvent(
                isEventActive = false,
                timeLeft = 0,
                teaserReceived = true
            )
        }

        fun activeEventWithTeaser(): MafiaSpecialEvent {
            return MafiaSpecialEvent(
                isEventActive = true,
                timeLeft = 6000,
                teaserReceived = false
            )
        }
    }
}

fun MafiaSpecialEvent.toResponse(): List<String> {
    return listOf(
        isEventActive.toInt().toString(),
        timeLeft.toString(),
        teaserReceived.toInt().toString(),
    )
}
