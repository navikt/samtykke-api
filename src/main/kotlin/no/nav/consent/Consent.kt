package no.nav.consent

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import no.nav.candidate.Candidate
import no.nav.employee.Employee

interface IConsentBase {
    val title: String
    val responsibleGroup: String
    val theme: String
    val purpose: String
    val totalInvolved: Int
    val expiration: LocalDate
    val endResult: String
}

@Serializable
data class ConsentBase(
    override val title: String,
    override val responsibleGroup: String,
    override val theme: String,
    override val purpose: String,
    override val totalInvolved: Int,
    override val expiration: LocalDate,
    override val endResult: String,
) : IConsentBase

@Serializable
data class CreateConsentRequest(
    override val title: String,
    override val responsibleGroup: String,
    override val theme: String,
    override val purpose: String,
    override val totalInvolved: Int,
    override val expiration: LocalDate,
    override val endResult: String,
    val slackChannelId: String
) : IConsentBase

@Serializable
data class FullConsent(
    val id: Long,
    override val title: String,
    override val responsibleGroup: String,
    override val theme: String,
    override val purpose: String,
    override val totalInvolved: Int,
    override val expiration: LocalDate,
    override val endResult: String,
    val slackChannelId: String,
    val code: String,
    val candidates: List<Candidate>?,
    val employee: Employee?
) : IConsentBase