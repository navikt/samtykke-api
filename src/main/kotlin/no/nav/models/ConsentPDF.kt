package no.nav.models

import kotlinx.serialization.Serializable

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
    val consent: BaseConsent,
    val employee: PDFEmployee,
    val candidate: PDFCandidate
)