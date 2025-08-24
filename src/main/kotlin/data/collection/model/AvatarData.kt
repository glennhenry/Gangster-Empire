package dev.gangster.data.collection.model

import dev.gangster.game.model.components.PortraitData
import dev.gangster.game.model.protobuf.common.PBCharacterClass
import dev.gangster.game.model.protobuf.common.PBGender

/**
 * Represent server-level data of avatar created by player.
 */
data class AvatarData(
    val gender: PBGender,
    val characterClass: PBCharacterClass,
    val portraitData: PortraitData
)
