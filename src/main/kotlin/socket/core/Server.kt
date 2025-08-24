package dev.gangster.socket.core

import dev.gangster.SERVER_HOST
import dev.gangster.SOCKET_SERVER_PORT
import dev.gangster.context.GlobalContext
import dev.gangster.game.model.components.AttributeCostsData
import dev.gangster.game.model.vo.AchievementVO
import dev.gangster.game.model.components.GoldConstantsData
import dev.gangster.game.model.components.toAucResponse
import dev.gangster.game.model.request.LreRequest
import dev.gangster.game.model.user.PlayerInfo
import dev.gangster.game.model.components.toPayload
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarRequest
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarResponse
import dev.gangster.game.model.protobuf.equipment.PBEquipmentGetArmamentPresetStatusResponse
import dev.gangster.game.model.protobuf.equipment.PBEquipmentViewArmamentResponse
import dev.gangster.game.model.protobuf.equipment.PBEquipmentViewFoodResponse
import dev.gangster.game.model.protobuf.equipment.PBEquipmentViewGearResponse
import dev.gangster.game.model.protobuf.equipment.PBEquipmentViewInventoryResponse
import dev.gangster.game.model.protobuf.misc.PBMiscNewAchievementsResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPaymentInfoResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPlayerCurrencyResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPlayerProfileResponse
import dev.gangster.game.model.protobuf.mission.PBMissionBoosterGetPlayerBoosterResponse
import dev.gangster.game.model.protobuf.mission.PBMissionBoosterShowMissionBoosterResponse
import dev.gangster.game.model.protobuf.mission.PBMissionViewResponse
import dev.gangster.game.model.protobuf.quest.PBQuestGetActiveQuestsResponse
import dev.gangster.game.model.protobuf.shop.PBShopViewItemsResponse
import dev.gangster.game.model.protobuf.work.PBWorkViewWorkResponse
import dev.gangster.game.model.response.LfeResponse
import dev.gangster.game.model.response.PngResponse
import dev.gangster.game.model.response.SaeResponse
import dev.gangster.game.model.response.toResponse
import dev.gangster.game.model.user.MafiaGangData
import dev.gangster.game.model.user.MafiaUserData
import dev.gangster.game.model.user.toOudResponse
import dev.gangster.game.model.user.toPayload
import dev.gangster.game.model.user.toResponse
import dev.gangster.game.model.vo.toPayload
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.socket.protocol.SmartFoxXML
import dev.gangster.game.data.AdminData
import dev.gangster.utils.Logger
import dev.gangster.utils.UUID
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.encoding.Base64

const val POLICY_REQUEST =
    "<cross-domain-policy><allow-access-from domain='*' to-ports='7777' /></cross-domain-policy>\u0000"

class Server(
    private val host: String = SERVER_HOST,
    private val port: Int = SOCKET_SERVER_PORT,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) {
    private val clients = ConcurrentHashMap<String, Connection>() // connectionId: Connection

    fun start() {
        coroutineScope.launch {
            try {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val serverSocket = aSocket(selectorManager).tcp().bind(host, port)
                Logger.info { "Socket server started at $host:$port" }

                while (true) {
                    val socket = serverSocket.accept()
                    val connection = Connection(
                        connectionId = UUID.new(),
                        socket = socket,
                        output = socket.openWriteChannel(autoFlush = true),
                    )
                    Logger.info { "New client: ${connection.socket.remoteAddress}" }
                    clients[connection.connectionId]
                    handleClient(connection)
                }
            } catch (e: Exception) {
                Logger.error { "ERROR on server: $e" }
                shutdown()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun handleClient(connection: Connection) {
        coroutineScope.launch {
            val socket = connection.socket
            val input = socket.openReadChannel()

            try {
                val buffer = ByteArray(4096)

                while (true) {
                    val bytesRead = input.readAvailable(buffer, 0, buffer.size)
                    if (bytesRead <= 0) break

                    val data = buffer.copyOfRange(0, bytesRead)
                    Logger.debug { "Received raw: ${data.decodeToString()}" }

                    when {
                        // Version check handshake (follows original smartfox)
                        data.startsWithString("<msg t='sys'><body action='verChk'") -> {
                            connection.sendRaw(POLICY_REQUEST.toByteArray()) // first request so response this first
                            connection.sendRaw(SmartFoxXML.apiOK())
                        }

                        // Handle server login (game uses extension for login response)
                        data.startsWithString("<msg t='sys'><body action='login'") -> {
                            val r = -1             // param0
                            val roomId = 1         // param1
                            val userCount = 1      // param2
                            val maxUsers = 100     // param3
                            val flags = 2          // param4 flags for room.
                            // (_loc3_ >> 1 & 1) = temp  [1 if flags=2]
                            // (_loc3_ >> 2 & 1) = game  [0 if flags=2]
                            // (_loc3_ >> 0 & 1) = priv  [0 if flags=2]
                            // (_loc3_ >> 3 & 1) = limbo [0 if flags=2]

                            val roomName = "Lobby" // param5 must be lobby
                            connection.sendRaw(
                                SmartFoxString.makeXt(
                                    "rlu", r, roomId, userCount,
                                    maxUsers, flags, roomName
                                )
                            )
                        }

                        // Handle room list
                        data.startsWithString("<msg t='sys'><body action='autoJoin'") -> {
                            connection.sendRaw(SmartFoxXML.joinOk(r = 1, pid = 0))
                        }

                        // Response to roundTrip message (likely first periodic ping)
                        data.startsWithString("<msg t='sys'><body action='roundTrip'") -> {
                            connection.sendRaw(SmartFoxXML.roundTripResponse())
                        }

                        // Response to periodic ping which is sent after the first roundTrip
                        // follows zone name in 1.xml
                        data.startsWithString("%xt%MafiaEx%pin") -> {
                            Logger.debug { "Received xt pin message" }
                        }

                        // Handle version check (again)
                        data.startsWithString("%xt%MafiaEx%vck") -> {
                            val r = 1
                            val unknown1 = 0
                            val unknown2 = 0
                            connection.sendRaw(
                                SmartFoxString.makeXt("vck", r, unknown1, unknown2)
                            )
                        }

                        // Handle create avatar
                        data.startsWithString("%xt%MafiaEx%createavatar") -> {
                            val xtReq = SmartFoxString.parsePbXt(data)
                            val pbRequest = ProtoBuf.decodeFromByteArray<PBCreateAvatarRequest>(xtReq.pbPayload)
                            Logger.debug { "Received createavatar request: $pbRequest" }

                            val pbResponse = PBCreateAvatarResponse(result = 1)
                            val xtRes = SmartFoxString.makeXt(
                                "createavatar",
                                xtReq.reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
                            )
                            connection.sendRaw(xtRes)
                        }

                        // Handle login register (new player)
                        data.startsWithString("%xt%MafiaEx%lre") -> {
                            val (xtReq, lreRequest) = SmartFoxString.parseObjXt<LreRequest>(data)
                            Logger.debug { "Received lre request: $lreRequest" }

                            val likelyStatusCodeWhere0IsSuccess = 0
                            val userId = AdminData.PLAYER_ID_NUMBER
                            val playerId = AdminData.PLAYER_ID_NUMBER
                            val xtRes1 = SmartFoxString.makeXt(
                                "lre",
                                xtReq.reqId,
                                likelyStatusCodeWhere0IsSuccess,
                                userId,
                                playerId,
                            )
                            connection.sendRaw(xtRes1)
                        }

                        // Handle login
                        data.startsWithString("%xt%MafiaEx%lgn") -> {
                            val (xtReq, ) = SmartFoxString.parseObjXt<LreRequest>(data)
                            Logger.debug { "Received lgn request: " }

                            val xtRes1 = SmartFoxString.makeXt("lgn", xtReq.reqId, 0, 315, 48343, 0, 0, 0)
                            connection.sendRaw(xtRes1)
                        }

                        // to send in order:
                        // *oga, *sgc, *oio, *playerprofile, *newachievements
                        // *paymentinfo, *oud, *playercurrency, *viewarmament, *getarmamentpresetstatus,
                        // *viewgear, *viewfood, *viewinventory, *viewitems, *viewitems, *viewitems, *auc,
                        // *getplayerbooster, *showmissionbooster, *viewmissions, *viewwork,
                        // *png, *sae, *lfe, *gch, *gfl, *getactivequests, *sgs, *sga, *apd

                        // apd is supposed to be send when the all data is sent to game
                        data.startsWithString("%xt%MafiaEx%apd") -> {
                            val apdXtRequest = SmartFoxString.parseXt(data) // empty payload, only reqId
                            val reqId = apdXtRequest.reqId
                            val statusCodeSuccess = 0
                            val playerId = 1

                            // prepare data...
                            /* OGA */
                            val ogaRes = SmartFoxString.makeXt(
                                "oga",
                                reqId,
                                statusCodeSuccess,
                                playerId,
                                AchievementVO.dummyAll().toPayload()
                            )
                            connection.sendRaw(ogaRes)

                            /* SGC */
                            val sgcRes = SmartFoxString.makeXt(
                                "sgc",
                                reqId,
                                statusCodeSuccess,
                                GoldConstantsData().toPayload()
                            )
                            connection.sendRaw(sgcRes)

                            /* OIO */
                            val oioRes = SmartFoxString.makeXt(
                                "oio",
                                reqId,
                                statusCodeSuccess,
                                PlayerInfo(
                                    email = AdminData.EMAIL,
                                    emailVerified = false,
                                    tutorialCompleted = false
                                ).toPayload()
                            )
                            connection.sendRaw(oioRes)

                            /* playerprofile */
                            val playerProfilePbResponse = PBMiscPlayerProfileResponse.dummy()
                            val playerprofileRes = SmartFoxString.makeXt(
                                "playerprofile",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(playerProfilePbResponse))
                            )
                            connection.sendRaw(playerprofileRes)

                            /* newachievements */
                            val newAchievementsPbResponse = PBMiscNewAchievementsResponse.empty()
                            val newAchievementsRes = SmartFoxString.makeXt(
                                "newachievements",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(newAchievementsPbResponse))
                            )
                            connection.sendRaw(newAchievementsRes)

                            /* paymentinfo */
                            val paymentInfoPbResponse = PBMiscPaymentInfoResponse.dummy()
                            val paymentInfoRes = SmartFoxString.makeXt(
                                "paymentinfo",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(paymentInfoPbResponse))
                            )
                            connection.sendRaw(paymentInfoRes)

                            /* oud */
                            val oudXtResponse = SmartFoxString.makeXt(
                                "oud",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                AdminData.PLAYER_ID_NUMBER,
                                MafiaUserData.dummy().toOudResponse()
                            )
                            connection.sendRaw(oudXtResponse)

                            /* playercurrency (player's resources, cash, gold) */
                            val playerCurrencyPbResponse = PBMiscPlayerCurrencyResponse.dummy()
                            val playerCurrencyRes = SmartFoxString.makeXt(
                                "playercurrency",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(playerCurrencyPbResponse))
                            )
                            connection.sendRaw(playerCurrencyRes)

                            /* viewarmament (the preset of equipment) */
                            val viewArmamentPbResponse = PBEquipmentViewArmamentResponse.dummy(AdminData.PLAYER_ID_NUMBER)
                            val viewArmamentRes = SmartFoxString.makeXt(
                                "viewarmament",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewArmamentPbResponse))
                            )
                            connection.sendRaw(viewArmamentRes)

                            /* getarmamentpresetstatus */
                            val getArmamentPresetStatusPbResponse = PBEquipmentGetArmamentPresetStatusResponse.dummy()
                            val getArmamentPresetStatusRes = SmartFoxString.makeXt(
                                "getarmamentpresetstatus",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(getArmamentPresetStatusPbResponse))
                            )
                            connection.sendRaw(getArmamentPresetStatusRes)

                            /* viewgear (the 8 item that increases attributes) */
                            val viewGearPbResponse = PBEquipmentViewGearResponse.empty(AdminData.PLAYER_ID_NUMBER)
                            val viewGearRes = SmartFoxString.makeXt(
                                "viewgear",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewGearPbResponse))
                            )
                            connection.sendRaw(viewGearRes)

                            /* viewfood (player food booster) */
                            val viewFoodPbResponse = PBEquipmentViewFoodResponse.empty(AdminData.PLAYER_ID_NUMBER)
                            val viewFoodRes = SmartFoxString.makeXt(
                                "viewfood",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewFoodPbResponse))
                            )
                            connection.sendRaw(viewFoodRes)

                            /* viewinventory (player inventory) */
                            val viewInventoryPbResponse = PBEquipmentViewInventoryResponse.empty()
                            val viewInventoryRes = SmartFoxString.makeXt(
                                "viewinventory",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewInventoryPbResponse))
                            )
                            connection.sendRaw(viewInventoryRes)

                            /* viewitems (items in shop) blackmarket */
                            val viewItemsPbResponse1 = PBShopViewItemsResponse.dummyBlackMarket()
                            val viewItemsRes1 = SmartFoxString.makeXt(
                                "viewitems",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewItemsPbResponse1))
                            )
                            connection.sendRaw(viewItemsRes1)

                            /* viewitems (items in shop) consumables */
                            val viewItemsPbResponse2 = PBShopViewItemsResponse.dummyKiosk()
                            val viewItemsRes2 = SmartFoxString.makeXt(
                                "viewitems",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewItemsPbResponse2))
                            )
                            connection.sendRaw(viewItemsRes2)

                            /* viewitems (items in shop) kiosk */
                            val viewItemsPbResponse3 = PBShopViewItemsResponse.dummyConsumables()
                            val viewItemsRes3 = SmartFoxString.makeXt(
                                "viewitems",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewItemsPbResponse3))
                            )
                            connection.sendRaw(viewItemsRes3)

                            /* auc */
                            val aucXtResponse = SmartFoxString.makeXt(
                                "auc",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                AttributeCostsData().toAucResponse()
                            )
                            connection.sendRaw(aucXtResponse)

                            /* getplayerbooster */
                            val getPlayerBoosterPbResponse =
                                PBMissionBoosterGetPlayerBoosterResponse.empty(AdminData.PLAYER_ID_NUMBER)
                            val getPlayerBoosterRes = SmartFoxString.makeXt(
                                "getplayerbooster",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(getPlayerBoosterPbResponse))
                            )
                            connection.sendRaw(getPlayerBoosterRes)

                            /* showmissionbooster */
                            val showMissionBoosterPbResponse =
                                PBMissionBoosterShowMissionBoosterResponse.empty()
                            val showMissionBoosterRes = SmartFoxString.makeXt(
                                "showmissionbooster",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(showMissionBoosterPbResponse))
                            )
                            connection.sendRaw(showMissionBoosterRes)

                            /* viewmissions */
                            val viewMissionsPbResponse = PBMissionViewResponse.dummy()
                            val viewMissionsRes = SmartFoxString.makeXt(
                                "viewmissions",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewMissionsPbResponse))
                            )
                            connection.sendRaw(viewMissionsRes)

                            /* viewwork */
                            val viewWorkPbResponse = PBWorkViewWorkResponse.dummy()
                            val viewWorkRes = SmartFoxString.makeXt(
                                "viewwork",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(viewWorkPbResponse))
                            )
                            connection.sendRaw(viewWorkRes)

                            /* png or ping data */
                            val pngData = PngResponse.empty()
                            val pngXtResponse = SmartFoxString.makeXt(
                                "png",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                *pngData.toResponse().toTypedArray()
                            )
                            connection.sendRaw(pngXtResponse)

                            /* sae or special event data */
                            val saeData = SaeResponse.noEvent()
                            val saeXtResponse = SmartFoxString.makeXt(
                                "sae",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                *saeData.toResponse().toTypedArray()
                            )
                            connection.sendRaw(saeXtResponse)

                            /* lfe or login features data */
                            val lfeData = LfeResponse.empty()
                            val lfeXtResponse = SmartFoxString.makeXt(
                                "lfe",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                *lfeData.toResponse().toTypedArray()
                            )
                            connection.sendRaw(lfeXtResponse)

                            /* gch or payment hash (possibly unique code for payment, differ each session) */
                            // example hash: 33f8fa22f1b3426568dc387edf1a50bcbbed4e71bcf2d8
                            // no need to implement
                            val gchData = "payment-hash-123"
                            val gchXtResponse = SmartFoxString.makeXt(
                                "gch",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                gchData
                            )
                            connection.sendRaw(gchXtResponse)

                            /* gfl or crypted forum hash */
                            // unknown what is this for, forum is dead since 10 years ago anyway
                            // example: 022161899996ee4135ffc36686634feac177a712e2dd584cb9d6b5779548631e7e8d600cce29799600403a482ec73657e20cdff6760b33cf2bf814f27d3c4506c82f4a95a5f8f6caab26bfbeb9d25c65cf8e72b7ffbabb79
                            val gflData = "forum-hash-123"
                            val gflXtResponse = SmartFoxString.makeXt(
                                "gfl",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                gflData
                            )
                            connection.sendRaw(gflXtResponse)

                            /* getactivequests */
                            val getActiveQuestsPbResponse = PBQuestGetActiveQuestsResponse.dummy()
                            val getActiveQuestsRes = SmartFoxString.makeXt(
                                "getactivequests",
                                reqId,
                                -1, // signify protobuf mode
                                Base64.encode(GlobalContext.pb.encodeToByteArray(getActiveQuestsPbResponse))
                            )
                            connection.sendRaw(getActiveQuestsRes)

                            /* sgs or gang shop*/
                            val sgsData = MafiaGangData.empty()
                            val sgsXtResponse = SmartFoxString.makeXt(
                                "sgs",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                *sgsData.toResponse().toTypedArray()
                            )
                            connection.sendRaw(sgsXtResponse)

                            /* sga or gang owned by player*/
                            val sgaData = MafiaGangData.empty()
                            val sgaXtResponse = SmartFoxString.makeXt(
                                "sga",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                                AdminData.PLAYER_ID_NUMBER,
                                *sgaData.toResponse().toTypedArray()
                            )
                            connection.sendRaw(sgaXtResponse)

                            // send apd (ready message)
                            val apdXtResponse = SmartFoxString.makeXt(
                                "apd",
                                apdXtRequest.reqId,
                                statusCodeSuccess,
                            )
                            connection.sendRaw(apdXtResponse)
                        }
                    }

                    Logger.info("<------------ SOCKET MESSAGE END ------------>")
                }
            } catch (e: Exception) {
                Logger.error { "Error in socket for ${connection.socket.remoteAddress}: $e" }
            } finally {
                Logger.info { "Client ${connection.socket.remoteAddress} disconnected" }
                connection.shutdown()
            }
        }
    }

    fun shutdown() {
        Logger.info { "Server closed." }
    }
}

fun ByteArray.startsWithBytes(prefix: ByteArray): Boolean {
    if (this.size < prefix.size) return false
    for (i in prefix.indices) {
        if (this[i] != prefix[i]) return false
    }
    return true
}

fun ByteArray.startsWithString(prefix: String, charset: Charset = Charsets.UTF_8): Boolean {
    val prefixBytes = prefix.toByteArray(charset)
    if (this.size < prefixBytes.size) return false
    for (i in prefixBytes.indices) {
        if (this[i] != prefixBytes[i]) return false
    }
    return true
}
