package dev.gangster.model

/**
 * SGC (gold constants used in-game as price)
 * [example: %xt%sgc%1%0%625+125+125+125+1250+125+625+625+125+125+125+125+625+125+250+250%%]
 *
 * each is separated by +
 */
data class GoldConstantsData(
    val createClanPrice: Int = 1,
    val changeAvatarPrice: Int = 1,
    val fillEnergyPrice: Int = 1,
    val cutMissionPricePerSecond: Int = 1,
    val maxSlotMachineInput: Int = 1,
    val cutNextFreeDuelTimePricePerSecond: Int = 1,
    val cutNextFreeWantedTimePricePerSecond: Int = 1,
    val cutNextFreeSurvivalTimePricePerSecond: Int = 1,
    val cutNextFreeDungeonTimePricePerSecond: Int = 1,
    val newShopItemsPrice: Int = 1,
    val newKioskItemsPrice: Int = 1,
    val cutFlightPricePerSecond: Int = 1,
    val emailVerificationGold: Int = 1,
    val levelUpGold: Int = 1,
    val tutorialReward: Int = 1,
    val unlockGangMemberSlot: Int = 1,
    val bailCost: Int = 1,
)

fun GoldConstantsData.toPayload(): String {
    return "$createClanPrice+" +
            "$changeAvatarPrice+" +
            "$fillEnergyPrice+" +
            "$cutMissionPricePerSecond+" +
            "$maxSlotMachineInput+" +
            "$cutNextFreeDuelTimePricePerSecond+" +
            "$cutNextFreeWantedTimePricePerSecond+" +
            "$cutNextFreeSurvivalTimePricePerSecond+" +
            "$cutNextFreeDungeonTimePricePerSecond+" +
            "$newShopItemsPrice+" +
            "$newKioskItemsPrice+" +
            "$cutFlightPricePerSecond+" +
            "$emailVerificationGold+" +
            "$levelUpGold+" +
            "$tutorialReward+" +
            "$unlockGangMemberSlot+" +
            "$bailCost"
}
