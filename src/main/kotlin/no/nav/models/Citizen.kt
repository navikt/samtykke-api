package no.nav.models

import kotlinx.serialization.Serializable

@Serializable
data class Citizen(
    val id: String,
    val candidatures: List<Candidate>
)
