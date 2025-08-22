package dev.gangster.model.vo

import kotlinx.serialization.Serializable

/**
 * the index and id start from 1 to 5
 * each int is id reference i think
 */
@Serializable
data class GangVO(
    val index: Int = 1,
    val id: Int = 1,
    val picID: Int = 0,    // pic id 0 = nothing
    val costC1: Int = 0,   // number
    val costC2: Int = 0,   // number
    val effectType: Int = 0,
    val effectValue: Int = 0,
    val starCount: Int = 0,
    val endTime: Int = 0,   // number
    val duration: Int = 0,   // number
    val remainingTime: String,
    val remainingTimeInSeconds: Int,
) {
    companion object {
        fun dummy(index: Int = 1, id: Int = 1): GangVO {
            return GangVO(
                index = index,
                id = id,
                remainingTime = "12345678",
                remainingTimeInSeconds = 12345
            )
        }
    }
}

fun GangVO.toResponse(): String {
    return "$id+$picID+$costC1+$costC2+$effectType+$effectValue+$starCount+$duration+$remainingTime+$remainingTimeInSeconds"
}
