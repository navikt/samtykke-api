package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.getCitizenId
import no.nav.models.Candidate
import no.nav.models.CreateCandidateRequest
import no.nav.services.CandidateService
import no.nav.services.CitizenService
import no.nav.services.ConsentService

fun Route.citizenRoute(
    consentService: ConsentService,
    candidateService: CandidateService,
    citizenService: CitizenService
) {
    route("consent") {
        route("active") {
            get {
                val activeConsents = consentService.getCitizenActiveConsents(getCitizenId(call.principal(), citizenService))
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
                    val consent = consentService.getConsentByCodeWithCandidate(code, getCitizenId(call.principal(), citizenService))
                    call.respond(consent)
                }

                post {
                    val source = call.receive<CreateCandidateRequest>()
                    val code = call.parameters["code"].toString()
                    candidateService.createCandidature(source, code, getCitizenId(call.principal(), citizenService))
                    call.respond(HttpStatusCode.OK)
                }
                put {
                    val newCandidate = call.receive<Candidate>()
                    val code = call.parameters["code"].toString()
                    candidateService.updateCandidate(newCandidate, code, getCitizenId(call.principal(), citizenService))
                    call.respond(HttpStatusCode.OK)
                }

                route("anonymize") {
                    put {
                        val code = call.parameters["code"].toString()
                        candidateService.anonymizeCandidate(code, getCitizenId(call.principal(), citizenService))
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}