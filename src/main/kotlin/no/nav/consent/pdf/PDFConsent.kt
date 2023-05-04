package no.nav.consent.pdf

import kotlinx.datetime.LocalDate
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
data class PDFConsent(
    val title: String,
    val responsibleGroup: String,
    val theme: String,
    val purpose: String,
    val totalInvolved: Int,
    val expiration: LocalDate,
    val endResult: String,
)

@Serializable
data class CreateConsentPDF(
    val version: PDFVersion,
    val consent: PDFConsent,
    val employee: PDFEmployee,
    val candidate: PDFCandidate
)