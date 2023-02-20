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
                try {
                    val code = call.parameters["code"].toString()
                    val consent = consentService.getConsentByCode(code)
                    call.respond(consent)
                } catch (e: Exception) {
                    call.respondText(
                        "Error getting consent",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }

        route("canditature") {
            get {
                // TODO: add
            }

            post {
                // TODO: add
                // TODO: check if citizen already has candidate, then dont allow to post
            }

            put {
                // TODO: add
            }
        }
    }
}