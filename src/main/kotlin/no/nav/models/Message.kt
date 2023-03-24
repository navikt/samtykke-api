package no.nav.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

enum class MessageType {
    CITIZEN_ACCEPT_CONSENT,
    CITIZEN_WITHDRAW_CONSENT,
    CITIZEN_UPDATE_CONSENT,
    CONSENT_EXPIRE
}

@Serializable
data class Message(
    val id: Long,
    val timestamp: LocalDate,
    val title: String,
    val description: String,
    val read: Boolean?,
    val ref: String?
)

@Serializable
data class BaseMessage(
    val title: String,
    val description: String,
    val ref: String?
)

