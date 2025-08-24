package dev.gangster.api

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileRoutes() {
    // game
    staticFiles("/game", File("static/game"))

    // crossdomain
    staticFiles("crossdomain.xml", File("static"))

    // subdomain re-route
    staticFiles("/files-ak", File("static/gangster-files"))
    staticFiles("/gangster-files", File("static/gangster-files"))

    staticFiles("/account", File("static/gangster-account"))
    staticFiles("/gangster-account", File("static/gangster-account"))

    staticFiles("/data", File("static/gangster-data"))
    staticFiles("/gangster-data", File("static/gangster-data"))

    staticFiles("/content", File("static/gangster-content"))
    staticFiles("/gangster-content", File("static/gangster-content"))

    // not yet requested, only seen in decompiled
    staticFiles("/cdn", File("static/cdn")) // CookieSaver.swf
}
