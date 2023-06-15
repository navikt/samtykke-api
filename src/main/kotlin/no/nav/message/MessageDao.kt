package no.nav.message

import io.ktor.server.plugins.*
import kotlinx.datetime.LocalDate
import no.nav.message.MessageDao.MessageQueries.POST_MESSAGE
import no.nav.message.MessageDao.MessageQueries.SELECT_ALL_MESSAGES_BY_EMPLOYEE
import no.nav.database.toList
import no.nav.message.MessageDao.MessageQueries.PATCH_MESSAGE
import javax.sql.DataSource

class MessageDao(
    private val dataSource: DataSource
) {
    fun getMessagesByEmployeeId(employeeId: String): List<Message> {
        dataSource.connection.use {
            return it.prepareStatement(SELECT_ALL_MESSAGES_BY_EMPLOYEE).apply {
                setString(1, employeeId)
            }.executeQuery().toList {
                Message(
                    getLong("id"),
                    LocalDate(
                        getDate("timestamp").toLocalDate().year,
                        getDate("timestamp").toLocalDate().month,
                        getDate("timestamp").toLocalDate().dayOfMonth
                    ),
                    getString("title"),
                    getString("description"),
                    getBoolean("read"),
                    getString("ref")
                )
            }
        }
    }

    fun createMessage(message: BaseMessage, employeeId: String) {
        dataSource.connection.use {
            it.prepareStatement(POST_MESSAGE).apply {
                setString(1, message.title)
                setString(2, message.description)
                setString(3, message.ref)
                setString(4, employeeId)
            }.executeUpdate()
        }
    }

    fun markMessageAsRead(messageId: Long) {
        try {
            dataSource.connection.use {
                it.prepareStatement(PATCH_MESSAGE).apply {
                    setBoolean(1, true)
                    setLong(2, messageId)
                }
            }
        } catch (e: Exception) {
            throw BadRequestException("Could not mark message as read")
        }
    }

    private object MessageQueries {
        val SELECT_ALL_MESSAGES_BY_EMPLOYEE = """
            SELECT * FROM message
            WHERE employee_id = ?
        """.trimIndent()

        val POST_MESSAGE= """
            INSERT INTO message
            (title, description, ref, employee_id)
            VALUES
            (?, ?, ?, ?)
        """.trimIndent()

        val PATCH_MESSAGE = """
            UPDATE message
            SET read = ?
            WHERE id = ?
        """.trimIndent()
    }
}