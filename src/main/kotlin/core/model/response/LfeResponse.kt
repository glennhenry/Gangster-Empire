package dev.gangster.core.model.response

import dev.gangster.core.model.vo.FeatureVO
import dev.gangster.core.model.vo.toResponse
import kotlinx.serialization.Serializable

/**
 * Lfe response
 *
 * example: %xt%lfe%1%0%28+1350288#27+1349078#26+1347868#25+1345449#24+1342684#23+1340179#22+1338451#21+1335340#20+1332403#19+1329984#18+1328169#17+1324281#16+1323331#15+1322726#14+1321862#13+1320307#12+1318406#%
 *
 */
@Serializable
data class LfeResponse(
    val features: List<FeatureVO>,
) {
    companion object {
        fun empty(): LfeResponse {
            return LfeResponse(
                features = emptyList()
            )
        }
    }
}

fun LfeResponse.toResponse(): List<Any> {
    val featuresStr = features.joinToString("#") { it.toResponse() }
    return listOf(
        "$featuresStr#" // client pop the last so keep trailing #
    )
}
