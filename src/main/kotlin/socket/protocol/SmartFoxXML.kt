package dev.gangster.socket.protocol

import dev.gangster.utils.XmlBuilder

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

    fun rmList(r: Int): String {
        return makeSys(
            action = "rmList", r = r,
            inner = XmlBuilder("rmList")
                .child("rm") {
                    attr("id", "0")
                    attr("maxu", "50")
                    attr("maxs", "5")
                    attr("temp", "0")
                    attr("game", "1")
                    attr("priv", "0")
                    attr("lmb", "0")
                    attr("ucnt", "0")
                    attr("scnt", "0")
                    child("n") { text("Lobbyx") }
                    child("vars") {}
                }
                .child("rm") {
                    attr("id", "1")
                    attr("maxu", "50")
                    attr("maxs", "5")
                    attr("temp", "0")
                    attr("game", "1")
                    attr("priv", "0")
                    attr("lmb", "0")
                    attr("ucnt", "2")
                    attr("scnt", "0")
                    child("n") { text("Gamex") }
                    child("vars") {
                        child("var") {
                            attr("n", "map")
                            attr("t", "s")
                            text("desert")
                        }
                        child("var") {
                            attr("n", "round")
                            attr("t", "n")
                            text("3")
                        }
                    }
                }
                .build()
        )
    }
}
