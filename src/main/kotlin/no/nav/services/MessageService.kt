package no.nav.services

import no.nav.database.dao.MessageDao
import no.nav.models.Message

class MessageService(
    private val messageDao: MessageDao
) {
    fun getMessagesByEmployeeId(employeeId: String): List<Message> = messageDao.getMessagesByEmployeeId(employeeId)
}