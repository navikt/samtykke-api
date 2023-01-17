package no.nav.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val timestamp: LocalDate,
    val title: String,
    val description: String,
    val read: Boolean,
    val ref: String?
)