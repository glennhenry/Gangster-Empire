package dev.gangster.socket.protocol

object SmartFoxString {
    fun makeXt(type: String, vararg msg: Any): String {
        return buildString {
            append("%xt")
            append("%$type")
            msg.forEach {
                append("%$it")
            }
            append("%\u0000")
        }
    }
}
