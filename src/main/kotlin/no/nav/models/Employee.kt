package no.nav.models

import kotlinx.serialization.Serializable
import no.nav.consent.Consent

@Serializable
data class Employee(
    val id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val consents: List<Consent>?,
    val messages: List<Message>?
)