package no.nav.services

import io.ktor.server.plugins.*
import no.nav.database.dao.MessageDao
import no.nav.models.Message

class MessageService(
    private val messageDao: MessageDao
) {
    fun getMessagesByEmployeeId(employeeId: String): List<Message> {
        val messages: List<Message> = messageDao.getMessagesByEmployeeId(employeeId)

        if (messages.isEmpty()) throw NotFoundException()

        return messages
    }
}