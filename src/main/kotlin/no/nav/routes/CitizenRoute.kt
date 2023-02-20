package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.CreateCandidateRequest
import no.nav.services.CandidateService
import no.nav.services.ConsentService

fun Route.citizenRoute(
    consentService: ConsentService,
    candidateService: CandidateService
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
            route("canditature") {
                get {
                    // 1. Get consent with code,
                    // 2. get candidate which is connected to both consent id and candidate it
                    try {
                        val code = call.parameters["code"].toString()
                        // TODO: replace this by getting id from TokenX token
                        val consent = consentService.getConsentByCodeWithCandidate(code, "sdp40972")
                        call.respond(consent)
                    } catch (e: Exception) {
                        call.respondText(
                            "Error getting consent",
                            status = HttpStatusCode.NotFound
                        )
                    }
                }

                post {
                    // TODO: Fix this returning only 200 OK
                    try {
                        val source = call.receive<CreateCandidateRequest>()
                        val code = call.parameters["code"].toString()
                        // TODO: replace this by getting id from TokenX token
                        candidateService.createCandidature(source, code, "sdp40972")
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotAcceptable)
                    }
                }

                put {
                    // TODO: add
                }
            }
        }
    }
}