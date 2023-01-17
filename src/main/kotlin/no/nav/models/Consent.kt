package no.nav.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Consent(
    val id: Long,
    val title: String,
    val responsibleGroup: String,
    val purpose: String,
    val totalInvolved: Int,
    val expiration: LocalDate,
    val code: String,
    val candidates: List<Candidate>
)