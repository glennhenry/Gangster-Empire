package dev.gangster.core.model.protobuf.quest

typealias PBQuestStatus = Int

object PBQuestStatusConstants {
    const val LOCKED = 1
    const val ACTIVE = 2
    const val PAUSED = 3
    const val FINISHED = 4
}
