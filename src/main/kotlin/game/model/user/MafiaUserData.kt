package dev.gangster.game.model.user

import kotlinx.serialization.Serializable

// MafiaUserData, or game data model dump

// 3x param1.shift after userMissionEnergy
// example: %xt%oud%1%0%4813360%0+18+6123+5116+6263+1000+-28421048+690+10+25+%
@Serializable
data class MafiaUserData(
    val userLevel: Int = 0,
    val userXP: Int,                 // number
    val userXPForCurrentLevel: Int,  // number
    val userXPForNextLevel: Int,     // number
    val userMissionEnergy: Int,
    val defense: Int                 // set to MafiaProfileData
) {
    companion object {
        fun dummy(): MafiaUserData {
            return MafiaUserData(
                userLevel = 1,
                userXP = 0,
                userXPForCurrentLevel = 0,
                userXPForNextLevel = 100,
                userMissionEnergy = 1000,
                defense = 25
            )
        }
    }
}

fun MafiaUserData.toOudResponse(): String {
    val ignored1 = 1000
    val ignored2 = -28421048
    val ignored3 = 690

    return buildString {
        append(0).append("+")
        append(userLevel).append("+")
        append(userXP).append("+")
        append(userXPForCurrentLevel).append("+")
        append(userXPForNextLevel).append("+")
        append(userMissionEnergy).append("+")
        append(ignored1).append("+")
        append(ignored2).append("+")
        append(ignored3).append("+")
        append(defense).append("+")
    }
}

