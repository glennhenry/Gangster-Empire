package dev.gangster.socket.handler.extension

import dev.gangster.context.ServerContext
import dev.gangster.game.model.request.LreRequest
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.utils.Logger
import kotlin.random.Random

/**
 * Register and login for new player.
 */
class XtLoginRegisterHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    private val APPROVED = 0
    private val SUGGEST_NAME_CHANGE = 5 // or 4
    private val NAME_ALREADY_EXIST = 6
    private val INVALID_EMAIL = 7
    private val PASSWORD_ISSUE = 9      // not sure
    private val PASSWORD_TOO_SHORT = 10 // if length < 4
    private val SERVER_ERROR = 11 // client doesn't check this

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_LOGIN_REGISTER
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        val request = SmartFoxString.parseObjXt<LreRequest>(message)
        Logger.debug { "Received login register request: $request" }

        var status = verifyUser(message.reqId, request)

        val payload = when (status) {
            SUGGEST_NAME_CHANGE -> {
                arrayOf(generateName())
            }

            NAME_ALREADY_EXIST -> {}
            INVALID_EMAIL -> {}
            PASSWORD_ISSUE -> {}
            PASSWORD_TOO_SHORT -> {}

            // APPROVED
            else -> {
                val result = serverContext.db.createPlayer(
                    username = request.username,
                    email = request.mail,
                    password = request.pw,
                    avatarData = connection.getAndClearAvatar()
                )
                if (result.isFailure) {
                    status = SERVER_ERROR
                    Logger.error { "Unknown server error when creating player: ${result.exceptionOrNull()}" }
                } else {
                    // [userId, playerId]
                    arrayOf(result.getOrThrow(), result.getOrThrow())
                }
            }
        }

        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_LOGIN_REGISTER,
            reqId = message.reqId,
            statusCode = status,
            mode = XtMode.Nothing,
            payload
        )

        connection.sendRaw(xtRes)
    }

    /**
     * Verify user and return status code
     */
    private suspend fun verifyUser(r: Int, req: LreRequest): Int {
        // Case: username too short
        if (req.username.length < 6) {
            return SUGGEST_NAME_CHANGE
        }

        // Case: username already exists
        val exists = serverContext.playerAccountRepository.doesUserExist(req.username)
        if (exists.getOrElse { false }) {
            return NAME_ALREADY_EXIST
        }

        // Case: invalid email (can enhance verification later)
        if (!req.mail.contains("@")) {
            return INVALID_EMAIL
        }

        // Case: password too short
        if (req.pw.length < 4) {
            return PASSWORD_TOO_SHORT
        }

        // Case: unknown password fails, skip.

        // Otherwise -> approved
        return APPROVED
    }

    private fun generateName(): String {
        val firstName = listOf("Ultimate", "Awesome", "Powerful", "Merciful", "Strong").random()
        val lastName = listOf("Gangster", "Mafia", "Boss", "Leader", "Dude").random()
        val number = Random.nextInt(1, 50)

        return "$firstName$lastName$number"
    }
}
