package no.nav.citizen

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import no.nav.candidate.Candidate
import no.nav.getCitizenId
import no.nav.consent.pdf.generateConsentPDF
import no.nav.candidate.CandidateService
import no.nav.candidate.CreateCandidateRequest
import no.nav.consent.ConsentService
import no.nav.consent.pdf.PDFVersion

@OptIn(InternalAPI::class)
fun Route.citizenRoute(
    consentService: ConsentService,
    candidateService: CandidateService,
    citizenService: CitizenService,
    httpClient: HttpClient
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

            route("pdf") {
                get {
                    val code = call.parameters["code"].toString()
                    val consent = consentService.getConsentByCodeWithCandidate(code, getCitizenId(call.principal(), citizenService))
                    val response = generateConsentPDF(httpClient, PDFVersion.CITIZEN, consent, consent.employee!!, consent.candidates!![0])
                    call.respond(response.content)
                }
            }
        }
    }
}