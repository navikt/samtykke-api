package no.nav.models

import kotlinx.serialization.Serializable
import no.nav.candidate.Candidate

@Serializable
data class Citizen(
    val id: String,
    val candidatures: List<Candidate>
)
