package dev.gangster.core.model.components

import kotlinx.serialization.Serializable

/**
 * for auc command
 * example: %xt%auc%1%0%1500+1500+1500+1500+0%
 */
@Serializable
data class AttributeCostsData(
    val attackCost: Int = 1500,
    val enduranceCost: Int = 1500,
    val luckCost: Int = 1500,
    val resistanceCost: Int = 1500,
    val freeAttributes: Int = 0
)

// response separated by +
fun AttributeCostsData.toAucResponse(): String {
    return "$attackCost+$enduranceCost+$luckCost+$resistanceCost+$freeAttributes"
}
