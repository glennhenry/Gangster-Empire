package dev.gangster.api

import dev.gangster.utils.Logger
import dev.gangster.utils.decodedUrl
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.apiRoutes() {
    post("/ftracking") {
        val body = call.receiveText()
        Logger.debug { "Received f tracking: $body" }
        call.respond(HttpStatusCode.OK)
    }

    post("/logging") {
        val body = call.receiveText().decodedUrl()
        Logger.debug(logFull = true) { "Received external logging: $body" }
        Logger.debug(logFull = true) { "TL;DR; ${body.substringAfter("logMessage").substringBefore("errorCode")}" }
        call.respond(HttpStatusCode.OK)
    }
}
