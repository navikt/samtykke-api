package no.nav.database.dao

import kotlinx.datetime.LocalDate
import no.nav.database.dao.MessageDao.MessageQueries.POST_MESSAGE
import no.nav.database.dao.MessageDao.MessageQueries.SELECT_ALL_MESSAGES_BY_EMPLOYEE
import no.nav.database.toList
import no.nav.models.BaseMessage
import no.nav.models.Message
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
    }
}