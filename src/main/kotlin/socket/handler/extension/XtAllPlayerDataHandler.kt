package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.data.collection.PlayerAccount
import dev.gangster.game.data.AdminData
import dev.gangster.game.model.components.AttributeCostsData
import dev.gangster.game.model.components.GoldConstantsData
import dev.gangster.game.model.components.toAucResponse
import dev.gangster.game.model.components.toPayload
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
import dev.gangster.game.model.user.PlayerInfo
import dev.gangster.game.model.user.toOudResponse
import dev.gangster.game.model.user.toPayload
import dev.gangster.game.model.user.toResponse
import dev.gangster.game.model.vo.AchievementVO
import dev.gangster.game.model.vo.toPayload
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlin.io.encoding.Base64

/**
 * apd or get all player data handler.
 *
 * It handles apd message by sending all player's data to client.
 * After all data is sent, it sent back the apd command.
 *
 * This is only used during game init, not intended to be used in the middle of gameplay.
 * Instead, you should use services class.
 *
 * Each expected data is separated to their own method (e.g., loadAchievements, loadShop),
 * where each method send message to client directly. The [handle] method should wait until all
 * method finishes.
 */
@OptIn(ExperimentalSerializationApi::class)
class XtAllPlayerDataHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    private var reqId: Int = 1
    private lateinit var connection: Connection
    private val SUCCESS = 0 // typically PB responses do not have status code

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        // Initialize context and services for player
        serverContext.playerContextRegistry.createContext(connection, serverContext.db, serverContext.config.useMongo)

        // Initialize local variables instead of passing them to each method.
        this.connection = connection
        this.reqId = message.reqId

        // Use services to load data
        val context = serverContext.requirePlayerContext(connection.playerId)

        // load each data and send them to client
        loadAchievements()
        loadGoldConstantsData()
        loadPlayerInfo(account = context.playerAccount, tutorialCompleted = false) // TO-DO replace
        loadPlayerProfile()
        loadNewAchievements()
        loadPaymentInfo()
        loadMafiaUserData()
        loadPlayerCurrency()
        loadArmament()
        loadArmamentPresetStatus()
        loadGear()
        loadFood()
        loadInventory()
        loadShopItems()
        loadAttributeCostsData()
        loadPlayerBooster()
        loadMissionBooster()
        loadMissions()
        loadWork()
        loadPing()
        loadSpecialEventData()
        loadLoginFeaturesData()
        loadPaymentHash()
        loadForumHash()
        loadActiveQuests()
        loadGangShop()
        loadPlayerGang()

        // send apd (ready message)
        val apdXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_ALL_PLAYER_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
        )
        connection.sendRaw(apdXtResponse)
    }


    /**
     * OGA, also known as avatar achievements, which load player's achievements.
     */
    private suspend fun loadAchievements() {
        val ogaRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_AVATAR_ACHIEVEMENTS,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            connection.playerId, // can change to other's playerId
            AchievementVO.dummyAll().toPayload() // TO-DO replace with player's achievement
        )
        connection.sendRaw(ogaRes)
    }

    /**
     * SGC, also known as gold constants data, corresponds to game's gold prices.
     */
    private suspend fun loadGoldConstantsData() {
        val sgcRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GOLD_CONSTANTS_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            GoldConstantsData().toPayload() // TO-DO make table
        )
        connection.sendRaw(sgcRes)
    }

    /**
     * OIO, which gets player's info.
     */
    private suspend fun loadPlayerInfo(account: PlayerAccount, tutorialCompleted: Boolean) {
        val oioRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_INFO,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            PlayerInfo(
                email = account.email,
                emailVerified = account.emailVerified,
                tutorialCompleted = tutorialCompleted
            ).toPayload()
        )
        connection.sendRaw(oioRes)
    }

    /**
     * playerprofile, which gets player's profile.
     */
    private suspend fun loadPlayerProfile() {
        val pbResponse = PBMiscPlayerProfileResponse.dummy() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_PROFILE,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * newachievements, which load player's new achievements (it appears to be similar with OGA).
     */
    private suspend fun loadNewAchievements() {
        val pbResponse = PBMiscNewAchievementsResponse.empty() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_NEW_ACHIEVEMENTS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * paymentinfo, which load player's payment information.
     *
     * not planning to implement.
     */
    private suspend fun loadPaymentInfo() {
        val pbResponse = PBMiscPaymentInfoResponse.dummy() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PAYMENT_INFO,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * OUD, which load mafia user data (kind of player's metadata).
     */
    private suspend fun loadMafiaUserData() {
        val oudXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_MAFIA_USER_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            connection.playerId,
            MafiaUserData.dummy().toOudResponse() // TO-DO replace
        )
        connection.sendRaw(oudXtResponse)
    }

    /**
     * playercurrency, which loads player's resources like gold and cash.
     */
    private suspend fun loadPlayerCurrency() {
        val pbResponse = PBMiscPlayerCurrencyResponse.dummy() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_CURRENCY,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewarmament, which loads player's armament or preset of equipment.
     */
    private suspend fun loadArmament() {
        val pbResponse = PBEquipmentViewArmamentResponse.dummy(connection.playerId) // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ARMAMENT,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * getarmamentpresetstatus, which loads preset status of player's equipment.
     */
    private suspend fun loadArmamentPresetStatus() {
        val pbResponse = PBEquipmentGetArmamentPresetStatusResponse.dummy() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GET_ARMAMENT_PRESET_STATUS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewgear, which loads player's gear (the 8 items that increases player's attributes)
     */
    private suspend fun loadGear() {
        val pbResponse = PBEquipmentViewGearResponse.empty(AdminData.PLAYER_ID_NUMBER)
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_GEAR,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewfood, which loads player's active food as boosters.
     */
    private suspend fun loadFood() {
        val pbResponse = PBEquipmentViewFoodResponse.empty(AdminData.PLAYER_ID_NUMBER) // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_FOOD,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewinventory, which loads player's inventory.
     */
    private suspend fun loadInventory() {
        val pbResponse = PBEquipmentViewInventoryResponse.empty() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_INVENTORY,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewitems, which loads shop items (black market, consumable, kiosk)
     */
    private suspend fun loadShopItems() {
        val blackMarketPbResponse = PBShopViewItemsResponse.dummyBlackMarket() // TO-DO make table
        val blackMarketXtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ITEMS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(blackMarketPbResponse))
        )
        connection.sendRaw(blackMarketXtRes)

        val consumablePbResponse = PBShopViewItemsResponse.dummyConsumables() // TO-DO make table
        val consumableXtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ITEMS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(consumablePbResponse))
        )
        connection.sendRaw(consumableXtRes)

        val kioskPbResponse = PBShopViewItemsResponse.dummyKiosk() // TO-DO make table
        val kioskXtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ITEMS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(kioskPbResponse))
        )
        connection.sendRaw(kioskXtRes)
    }

    /**
     * auc, which loads attributes (e.g., attack power, endurance) costs data.
     */
    private suspend fun loadAttributeCostsData() {
        val aucXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_ATTRIBUTE_COSTS_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            AttributeCostsData().toAucResponse() // TO-DO make table
        )
        connection.sendRaw(aucXtResponse)
    }

    /**
     * getplayerbooster, which loads player's booster.
     */
    private suspend fun loadPlayerBooster() {
        val pbResponse = PBMissionBoosterGetPlayerBoosterResponse.empty(connection.playerId) // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GET_PLAYER_BOOSTER,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * showmissionbooster, which loads mission booster.
     *
     * kind of unsure the difference with getplayerbooster.
     */
    private suspend fun loadMissionBooster() {
        val pbResponse = PBMissionBoosterShowMissionBoosterResponse.empty() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_SHOW_MISSION_BOOSTER,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewmissions, which loads player's available missions.
     */
    private suspend fun loadMissions() {
        val pbResponse = PBMissionViewResponse.dummy() // TO-DO replace
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_MISSIONS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewwork, which loads player's work progress.
     */
    private suspend fun loadWork() {
        val pbResponse = PBWorkViewWorkResponse.dummy()
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_WORK,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * png, which loads player's ping data (mission progress, duel cooldown, police, etc)
     */
    private suspend fun loadPing() {
        val pngData = PngResponse.empty() // TO-DO replace
        val pngXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PING,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *pngData.toResponse().toTypedArray()
        )
        connection.sendRaw(pngXtResponse)
    }

    /**
     * sae, which loads special event data.
     */
    private suspend fun loadSpecialEventData() {
        val saeData = SaeResponse.noEvent()
        val saeXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_SPECIAL_EVENT_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *saeData.toResponse().toTypedArray()
        )
        connection.sendRaw(saeXtResponse)
    }

    /**
     * lfe, which loads login features data.
     *
     * unsure what features are.
     */
    private suspend fun loadLoginFeaturesData() {
        val lfeData = LfeResponse.empty() // TO-DO replace
        val lfeXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_LOGIN_FEATURES_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *lfeData.toResponse().toTypedArray()
        )
        connection.sendRaw(lfeXtResponse)
    }

    /**
     * gch, which generate payment hash (possibly unique code for payment).
     *
     * no need to implement.
     */
    private suspend fun loadPaymentHash() {
        val gchData = "payment-hash-123"
        val gchXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PAYMENT_HASH,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            gchData
        )
        connection.sendRaw(gchXtResponse)
    }

    /**
     * gfl, which generate crypted forum hash.
     *
     * no need to implement.
     */
    private suspend fun loadForumHash() {
        val gflData = "forum-hash-123"
        val gflXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_CRYPTED_FORUM_HASH,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            gflData
        )
        connection.sendRaw(gflXtResponse)
    }

    /**
     * getactivequests, which loads player's active quests.
     */
    private suspend fun loadActiveQuests() {
        val pbResponse = PBQuestGetActiveQuestsResponse.dummy() // TO-DO replace and make table
        val getActiveQuestsRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GET_ACTIVE_QUESTS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(getActiveQuestsRes)
    }

    /**
     * sgs, which loads game's gang shop, place where you hire one of five gang in character panel.
     */
    private suspend fun loadGangShop() {
        val sgsData = MafiaGangData.empty() // TO-DO make table
        val sgsXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GANG_SHOP,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *sgsData.toResponse().toTypedArray()
        )
        connection.sendRaw(sgsXtResponse)
    }

    /**
     * sga, which loads gang owned by player.
     */
    private suspend fun loadPlayerGang() {
        val sgaData = MafiaGangData.empty() // TO-DO replace
        val sgaXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_GANG,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            connection.playerId,
            *sgaData.toResponse().toTypedArray()
        )
        connection.sendRaw(sgaXtResponse)
    }
}
