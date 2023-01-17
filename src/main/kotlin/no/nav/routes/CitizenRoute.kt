package no.nav.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.mocks.consentsMock

fun Route.citizenRoute() {
    route("/consent") {
        get("/active") {
            call.respond(listOf(consentsMock()[0], consentsMock()[1]))
        }
    }
}