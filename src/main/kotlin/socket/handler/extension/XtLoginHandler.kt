package dev.gangster.socket.handler.extension

import dev.gangster.context.ServerContext
import dev.gangster.game.model.request.LgnRequest
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.utils.Logger

class XtLoginHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    private val APPROVED = 0
    private val WRONG_CREDENTIALS_NAME = 1
    private val WRONG_CREDENTIALS_PASSWORD = 2
    private val PLAYER_BANNED = 3

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_LOGIN
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        val request = SmartFoxString.parseObjXt<LgnRequest>(message)
        Logger.debug { "Received lgn request: $request" }

        val (successCode, playerId) = verifyUser(request)
        val userId = playerId
        val rememberMe = 0   // 0: remembered (i.e., save to local data)

        // when player is inactive for long, give them bonuses;
        // which depends on how long they have been inactive.
        // lastLogin > x, add free drink and gold.
        // also synchronize with the part of code that response player data.
        val freeGold = 0
        val freeDrink = 0

        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_LOGIN,
            reqId = message.reqId,
            statusCode = successCode,
            mode = XtMode.Nothing,
            userId, playerId, rememberMe, freeGold, freeDrink
        )
        connection.sendRaw(xtRes)
    }

    /**
     * Verify user and return status code.
     *
     * @return pair of status code with playerId if login success.
     */
    private suspend fun verifyUser(req: LgnRequest): Pair<Int, Int?> {
        // Case: username not found
        val foundUsername = serverContext.playerAccountRepository.doesUserExist(req.name)
        if (foundUsername.getOrElse { false }) {
            return Pair(WRONG_CREDENTIALS_NAME, null)
        }

        // Case: password wrong
        val passwordMatch = serverContext.playerAccountRepository.verifyCredentials(req.name, req.pw)
        if (passwordMatch.getOrNull() != null) {
            return Pair(WRONG_CREDENTIALS_PASSWORD, null)
        }

        val playerId = passwordMatch.getOrThrow()

        // Case: player is banned
        val bannedTime = serverContext.playerAccountRepository.playerBanExpireAt(playerId)
        if (bannedTime.getOrNull() != null && bannedTime.getOrThrow() > 0) {
            return Pair(PLAYER_BANNED, null)
        }

        return Pair(APPROVED, playerId)
    }
}
