package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarRequest
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarResponse
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.utils.Logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.io.encoding.Base64

/**
 * Handle create avatar, which is when player choose class and mafia avatar.
 */
class XtCreateAvatarHandler : MessageHandler<XtMessage> {
    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_CREATE_AVATAR
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun handle(
        connection: Connection,
        message: XtMessage,
        send: suspend (ByteArray) -> Unit
    ) {
        val pbRequest = ProtoBuf.decodeFromByteArray<PBCreateAvatarRequest>(message.pbPayload)
        Logger.debug { "Received createavatar request: $pbRequest" }
        connection.saveAvatarTemporarily(pbRequest)

        val pbResponse = PBCreateAvatarResponse(result = 1)
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_CREATE_AVATAR,
            reqId = message.reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }
}
