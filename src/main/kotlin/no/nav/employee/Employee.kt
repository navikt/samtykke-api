package no.nav.employee

import kotlinx.serialization.Serializable
import no.nav.consent.FullConsent
import no.nav.message.Message

@Serializable
data class Employee(
    val id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val consents: List<FullConsent>?,
    val messages: List<Message>?
)