package dev.gangster.socket.protocol

import dev.gangster.socket.message.XmlBuilder

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

    // client used smartfox extension to replace rmlist during login response
    fun rmList(r: Int): String {
        return makeSys(
            action = "rmList", r = r,
            inner = XmlBuilder("rmList")
                .child("rm") {
                    attr("id", "0")
                    attr("maxu", "100")
                    attr("maxs", "5")
                    attr("temp", "0")
                    attr("game", "1")
                    attr("priv", "0")
                    attr("lmb", "0")
                    attr("ucnt", "0")
                    attr("scnt", "0")
                    child("n") { text("MafiaEx") }
                    child("vars") {}
                }
                .build()
        )
    }

    fun joinOk(r: Int, pid: Int): String {
        val inner = XmlBuilder("pid")
            .attr("id", pid.toString())
            .child("vars") {}
            .child("uLs") { attr("r", "1") }
            .build()

        return makeSys("joinOK", r, inner)
    }

    /**
     * Round trip response. I think it signify the start of periodic ping.
     */
    fun roundTripResponse(r: Int = 1): String {
        return makeSys(action = "roundTripRes", r = r, inner = "")
    }
}
