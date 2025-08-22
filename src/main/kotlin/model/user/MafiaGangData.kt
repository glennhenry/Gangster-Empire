package dev.gangster.model.user

import dev.gangster.model.vo.GangVO
import dev.gangster.model.vo.toResponse
import kotlinx.serialization.Serializable

/**
 * sgs or check gang shop which in turns parse each gang VO
 *
 * example: %xt%sgs%1%0%1+1+0+675+2+1+1+7+604800#2+2+0+500+0+50+1+7+604800#3+3+0+500+1+10+1+7+604800#4+4+0+325+3+2+1+7+604800#5+5+3500+0+4+2+0+7+604800#%
 */
@Serializable
data class MafiaGangData(
    val gangs: List<GangVO>
) {
    companion object {
        fun empty(): MafiaGangData {
            return MafiaGangData(
                gangs = emptyList()
            )
        }
    }
}

fun MafiaGangData.toResponse(): List<Any> {
    val gangsStr = gangs.joinToString("#") { it.toResponse() }
    return listOf(
        "$gangsStr#"
    )
}
