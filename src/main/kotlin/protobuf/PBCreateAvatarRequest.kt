package dev.gangster.protobuf

import kotlinx.serialization.Serializable

/**
 * male = 1, female = 2
 * bully = 1, rogue = 2, tactictian = 3
 *
 * first portrait structure: <gender>!<skinColor>~<hairColor>~<eyeColor>~<eyebrowColor>~<shirtColor>~<lipstickColor>~<extraTopColor>~<extraUnderColor>!
 * second portrait structure for male: backhair~body~jaw~beard~extraUnder~eyes~eyebrows~nose~mouth~extraTop~fronthair~background
 * second portrait structure for female: backhair~body~jaw~extraUnder~eyes~eyebrows~nose~mouth~extraTop~fronthair~background
 * where each part is index (see Constants_Avatarparts)
 *
 */
@Serializable
data class PBCreateAvatarRequest(
    val gender: Int,
    val characterClass: Int,
    val portrait: String,
)
