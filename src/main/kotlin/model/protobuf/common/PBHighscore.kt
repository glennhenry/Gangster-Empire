package dev.gangster.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBHighscore(
    val dynamic: PBHighscoreEntry,
    val alltime: PBHighscoreEntry
) {
    companion object {
        fun dummy(): PBHighscore {
            return PBHighscore(
                dynamic = PBHighscoreEntry.dummy(),
                alltime = PBHighscoreEntry.dummy()
            )
        }
    }
}

@Serializable
data class PBHighscoreEntry(
    val glory: Int,
    val rank: Int,
) {
    companion object {
        fun dummy(): PBHighscoreEntry {
            return PBHighscoreEntry(
                glory = 1,
                rank = 1
            )
        }
    }
}
