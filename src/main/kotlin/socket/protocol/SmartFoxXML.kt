package dev.gangster.socket.protocol

object SmartFoxXML {
    fun makeSys(action: String, r: Int = 0, inner: String = ""): String {
        return "<msg t='sys'><body action='$action' r='$r'>$inner</body></msg>\u0000"
    }

    fun apiOK(): String {
        return makeSys("apiOK")
    }

    fun logOK(userId: Int, mod: Int, name: String): String {
        return makeSys(
            "logOK",
            0,
            "<login id='$userId' mod='$mod' n='$name'/>"
        )
    }

    fun logKO(error: String): String {
        return makeSys(
            "logKO",
            0,
            "<login e='$error'/>"
        )
    }
}
