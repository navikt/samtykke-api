package no.nav.consent.pdf

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import no.nav.candidate.Candidate
import no.nav.consent.ConsentBase
import no.nav.consent.FullConsent
import no.nav.employee.Employee

fun generateConsentPDF(httpClient: HttpClient, version: PDFVersion, consent: FullConsent, employee: Employee, candidate: Candidate?): HttpResponse = runBlocking {

    val logger = KtorSimpleLogger("com.example.RequestTracePlugin")

    val pdfGeneratorAPIPath = "${System.getenv("PDFGEN_URL")}/api/v1/genpdf/samtykke/samtykke"

    try {
        when (version) {
            PDFVersion.EMPLOYEE -> {
                return@runBlocking httpClient.post {
                    url(pdfGeneratorAPIPath)
                    contentType(ContentType.Application.Json)
                    setBody(CreateConsentPDF(
                        version,
                        ConsentBase(
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
                        ConsentBase(
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
        logger.info("PDF of consent could not be generated: ${e.message}")
        throw BadRequestException("PDF could not be generated")
    }
}