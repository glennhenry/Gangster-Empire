package dev.gangster.socket.protocol

import dev.gangster.socket.message.SfMessage
import dev.gangster.socket.message.XmlBuilder
import dev.gangster.utils.toInt
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

object SmartFoxXML {
    fun makeSys(action: String, r: Int = 0, inner: String = ""): String {
        return "<msg t='sys'><body action='$action' r='$r'>$inner</body></msg>\u0000"
    }

    fun parse(xml: String): SfMessage {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc: Document = builder.parse(xml.byteInputStream())

        // <msg t="...">
        val msgElement = doc.documentElement
        val type = msgElement.getAttribute("t")

        // <body action="..." r="...">
        val bodyElement = msgElement.getElementsByTagName("body")
            .item(0) as Element
        val action = bodyElement.getAttribute("action")
        val reqId = bodyElement.getAttribute("r").toIntOrNull() ?: 0

        // Inner XML/text inside <body> … </body>
        val inner = bodyElement.childNodes.let { nodeList ->
            buildString {
                for (i in 0 until nodeList.length) {
                    val node = nodeList.item(i)
                    append(node.toString())
                }
            }.trim()
        }

        return SfMessage(
            type = type,
            action = action,
            reqId = reqId,
            body = inner
        )
    }

    fun apiOK(): String {
        return makeSys("apiOK")
    }

    fun logOK(r: Int, userId: Int, isMod: Boolean, name: String): String {
        return makeSys("logOK", r, "<login id='$userId' mod='${isMod.toInt()}' n='$name'/>")
    }

    fun logKO(r: Int, error: String): String {
        return makeSys("logKO", r, "<login e='$error'/>")
    }

    fun joinOk(r: Int, pid: Int): String {
        val inner = XmlBuilder("pid")
            .attr("id", pid.toString())
            .child("vars") {}
            .child("uLs") { attr("r", r.toString()) }
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
