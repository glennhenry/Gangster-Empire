package dev.gangster.socket.message

import dev.gangster.socket.handler.DefaultSfHandler
import dev.gangster.socket.handler.DefaultXtHandler
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.utils.Logger

/**
 * Dispatch client message to a registered [MessageHandler].
 */
class MessageDispatcher() {
    private val sfHandlers = mutableListOf<MessageHandler<SfMessage>>()
    private val xtHandlers = mutableListOf<MessageHandler<XtMessage>>()

    private val defaultSf = DefaultSfHandler()
    private val defaultXt = DefaultXtHandler()

    fun registerSf(handler: MessageHandler<SfMessage>) = sfHandlers.add(handler)
    fun registerXt(handler: MessageHandler<XtMessage>) = xtHandlers.add(handler)

    fun findSfHandler(msg: SfMessage): MessageHandler<SfMessage> {
        Logger.info { "Finding SF handler for msg: $msg" }
        return sfHandlers.find {
            it.match(msg)
        } ?: defaultSf
    }

    fun findXtHandler(msg: XtMessage): MessageHandler<XtMessage> {
        Logger.info { "Finding XT handler for msg: $msg" }
        return xtHandlers.find {
            it.match(msg)
        } ?: defaultXt
    }

    fun close() {
        sfHandlers.clear()
        xtHandlers.clear()
    }
}
