package no.nav.consent.pdf

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import kotlinx.coroutines.runBlocking
import no.nav.candidate.Candidate
import no.nav.consent.Consent
import no.nav.employee.Employee

fun generateConsentPDF(httpClient: HttpClient, version: PDFVersion, consent: Consent, employee: Employee, candidate: Candidate?): HttpResponse = runBlocking {
    val pdfGeneratorAPIPath = "${System.getenv("PDFGEN_URL")}/api/v1/genpdf/samtykke/samtykke"

    try {
        when (version) {
            PDFVersion.EMPLOYEE -> {
                return@runBlocking httpClient.post {
                    url(pdfGeneratorAPIPath)
                    contentType(ContentType.Application.Json)
                    setBody(CreateConsentPDF(
                        version,
                        PDFConsent(
                            consent.title,
                            consent.responsibleGroup,
                            consent.theme,
                            consent.purpose,
                            consent.totalInvolved,
                            consent.expiration,
                            consent.endResult
                        ),
                        PDFEmployee(
                            employee.firstname,
                            employee.lastname,
                            employee.email
                        ),
                        PDFCandidate(
                            "",
                            "",
                            false
                        )
                    ))
                }
            }
            PDFVersion.CITIZEN -> {
                return@runBlocking httpClient.post {
                    url(pdfGeneratorAPIPath)
                    contentType(ContentType.Application.Json)
                    setBody(CreateConsentPDF(
                        version,
                        PDFConsent(
                            consent.title,
                            consent.responsibleGroup,
                            consent.theme,
                            consent.purpose,
                            consent.totalInvolved,
                            consent.expiration,
                            consent.endResult
                        ),
                        PDFEmployee(
                            employee.firstname,
                            employee.lastname,
                            employee.email
                        ),
                        PDFCandidate(
                            candidate!!.name,
                            candidate.email,
                            candidate.audioRecording
                        )
                    ))
                }
            }
        }
    } catch (e: Exception) {
        throw BadRequestException("PDF could not be generated")
    }
}