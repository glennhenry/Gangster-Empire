package dev.gangster.model.request

import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.Serializable

/**
 * LRE or login/register request
 *
 * @property mail
 * @property pw
 * @property referrer
 * @property lang
 * @property did distributorId
 * @property connectTime connectionTime
 * @property ping roundTripTime
 * @property accountId accountId
 * @property campainPId partnerId
 * @property campainCr creative
 * @property campainPl placement
 * @property campainKey keyword
 * @property campainNW network
 * @property campainLP lp
 * @property campainCId channelId
 * @property campainTS trafficSource
 * @property adid aid
 * @property camp camp
 * @property adgr adgr
 * @property matchtype
 */
@Serializable
data class LreRequest(
    val username: String,
    val mail: String,
    val pw: String,
    val referrer: String = "",
    val lang: String = "en",
    val did: String = "1",
    val connectTime: Long = getTimeMillis(),
    val ping: Int = 0,
    val accountId: String = getTimeMillis().toString(),
    val campainPId: String? = null,
    val campainCr: String? = null,
    val campainPl: String? = null,
    val campainKey: String? = null,
    val campainNW: String? = null,
    val campainLP: String? = null,
    val campainCId: String? = null,
    val campainTS: String? = null,
    val adid: String? = null,
    val camp: String? = null,
    val adgr: String? = null,
    val matchtype: String? = null,
)