package no.nav.consent

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import no.nav.candidate.Candidate
import no.nav.employee.Employee

@Serializable
data class Consent(
    val id: Long,
    val title: String,
    val responsibleGroup: String,
    val theme: String,
    val purpose: String,
    val totalInvolved: Int,
    val expiration: LocalDate,
    val endResult: String,
    val slackChannelId: String,
    val code: String?,
    val candidates: List<Candidate>?,
    val employee: Employee?
)

@Serializable
data class BaseConsent(
    val title: String,
    val responsibleGroup: String,
    val theme: String,
    val purpose: String,
    val totalInvolved: Int,
    val expiration: LocalDate,
    val endResult: String,
    val slackChannelId: String,
)