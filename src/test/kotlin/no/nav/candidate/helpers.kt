package no.nav.candidate

import no.nav.models.Candidate
import no.nav.models.CandidateStatus

fun validateCandidateAnonymized(candidate: Candidate) {
    require(candidate.name.isBlank()) { "Name cannot be set" }
    require(candidate.email.isBlank()) { "Email cannot be set" }
    require(candidate.status == CandidateStatus.WITHDRAWN) { "Status must be WITHDRAWN" }
    require(candidate.consented == null) { "Consented date cannot be set" }
    require(!candidate.audioRecording) { "Audio recording must be set to false" }
}