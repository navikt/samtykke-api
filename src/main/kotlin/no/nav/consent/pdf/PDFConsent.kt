package no.nav.consent.pdf

import kotlinx.serialization.Serializable
import no.nav.consent.ConsentBase
import no.nav.consent.IConsentBase

enum class PDFVersion {
    EMPLOYEE, CITIZEN
}

@Serializable
data class PDFCandidate(
    val name: String,
    val email: String,
    val audioRecording: Boolean
)

@Serializable
data class PDFEmployee(
    val firstname: String,
    val lastname: String,
    val email: String
)

@Serializable
data class CreateConsentPDF(
    val version: PDFVersion,
    val consent: ConsentBase,
    val employee: PDFEmployee,
    val candidate: PDFCandidate
)