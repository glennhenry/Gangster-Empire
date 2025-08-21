package dev.gangster.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class Highscore(
    val dynamic: HighscoreEntry,
    val alltime: HighscoreEntry
) {
    companion object {
        fun dummy(): Highscore {
            return Highscore(
                dynamic = HighscoreEntry.dummy(),
                alltime = HighscoreEntry.dummy()
            )
        }
    }
}

@Serializable
data class HighscoreEntry(
    val glory: Int,
    val rank: Int,
) {
    companion object {
        fun dummy(): HighscoreEntry {
            return HighscoreEntry(
                glory = 1,
                rank = 1
            )
        }
    }
}
