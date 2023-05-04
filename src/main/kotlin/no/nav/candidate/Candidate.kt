package no.nav.candidate

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Candidate(
    val id: Long,
    val name: String,
    val email: String,
    val status: CandidateStatus,
    val consented: LocalDate?,
    val trackingNumber: String,
    val audioRecording: Boolean,
    val consentId: Long,
)

@Serializable
data class CreateCandidateRequest(
    val name: String,
    val email: String,
    val status: CandidateStatus,
    val consented: LocalDate,
    val audioRecording: Boolean,
)