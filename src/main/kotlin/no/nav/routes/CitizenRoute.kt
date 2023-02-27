package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.Candidate
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
                // TODO: replace this by getting id from TokenX token
                val activeConsents = consentService.getCitizenActiveConsents("sdp40972")
                call.respond(activeConsents)
            }
        }

        route("{code}") {
            get {
                val code = call.parameters["code"].toString()
                val consent = consentService.getConsentByCode(code)
                call.respond(consent)
            }

            route("canditature") {
                get {
                    val code = call.parameters["code"].toString()
                    // TODO: replace this by getting id from TokenX token
                    val consent = consentService.getConsentByCodeWithCandidate(code, "sdp40972")
                    call.respond(consent)
                }

                post {
                    val source = call.receive<CreateCandidateRequest>()
                    val code = call.parameters["code"].toString()
                    // TODO: replace this by getting id from TokenX token
                    candidateService.createCandidature(source, code, "sdp40972")
                    call.respond(HttpStatusCode.OK)
                }
                put {
                    val newCandidate = call.receive<Candidate>()
                    val code = call.parameters["code"].toString()
                    // TODO: replace this by getting id from TokenX token
                    candidateService.updateCandidate(newCandidate, code, "sdp40972")
                    call.respond(HttpStatusCode.OK)
                }

                route("anonymize") {
                    put {
                        val code = call.parameters["code"].toString()
                        candidateService.anonymizeCandidate(code, "sdp40972")
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}