package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.services.ConsentService

fun Route.citizenRoute(
    consentService: ConsentService
) {
    route("consent") {
        route("active") {
            get {
                try {
                    // TODO: replace this by getting id from TokenX token
                    val activeConsents = consentService.getCitizenActiveConsents("sdp40972")
                    call.respond(activeConsents)
                } catch (e: Exception) {
                    call.respondText(
                        "Error getting active consents",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }

        route("{code}") {
            get {
                // TODO: add
            }
        }

        route("canditature") {
            get {
                // TODO: add
            }

            post {
                // TODO: add
            }

            put {
                // TODO: add
            }
        }
    }
}