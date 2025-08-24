package dev.gangster.core.model.protobuf.quest

import kotlinx.serialization.Serializable

/**
 * getactivequests request
 *
 * example: %1%-1%CAESPwg5EAIYAiIMCEcQCRgEIAIoATABIgkIDhAGGJYBIBgiCAgYEAcYZCBCIggIQhANGAEgASoKCAAQABgAIAAwAxI0CAsQARgCIggIFhAGGAUgASIICC8QChgDIAMiCggPEAYYDCACKAEqCAgAEAAYACAAMAI4ABJOCCkQAxgCIggIQBAOGAEgACo8CLwFEAAYACAAKjEICxABGAQiBgiIExC0ASgAMgUIABCPTjgCUgQIAhACYAVoCnIMCA8Vj8L1PB3NzEw9GAA=%
 *
 * result:
 * ok = 1
 */
@Serializable
data class PBQuestGetActiveQuestsResponse(
    val result: Int,
    val quests: List<PBQuest>,
    val newQuestsAvailable: Boolean,   // turn this to true if new quests are added
) {
    companion object {
        fun dummy(): PBQuestGetActiveQuestsResponse {
            return PBQuestGetActiveQuestsResponse(
                result = 1,
                quests = listOf(PBQuest.dummy()),
                newQuestsAvailable = true
            )
        }
    }
}
