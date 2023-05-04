package no.nav.employee

import io.ktor.server.plugins.*
import no.nav.employee.EmployeeDao.EmployeeQueries.POST_EMPLOYEE
import no.nav.employee.EmployeeDao.EmployeeQueries.SELECT_EMPLOYEE
import javax.sql.DataSource

class EmployeeDao(
    private val dataSource: DataSource
) {
    fun getEmployee(employeeId: String): Employee {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_EMPLOYEE).apply {
                setString(1, employeeId)
            }.executeQuery()
            return if (result.next()) {
                Employee(
                    result.getString("id"),
                    result.getString("firstname"),
                    result.getString("lastname"),
                    result.getString("email"),
                    null,
                    null
                )
            } else {
                throw NotFoundException()
            }
        }
    }

    fun createEmployee(employee: Employee) {
        dataSource.connection.use {
            it.prepareStatement(POST_EMPLOYEE).apply {
                setString(1, employee.id)
                setString(2, employee.firstname)
                setString(3, employee.lastname)
                setString(4, employee.email)
            }.executeUpdate()
        }
    }

    private object EmployeeQueries {
        val SELECT_EMPLOYEE = """
            SELECT *
            FROM employee
            WHERE id = ?
        """.trimIndent()

        val POST_EMPLOYEE = """
            INSERT INTO employee
            (id, firstname, lastname, email)
            VALUES
            (?, ?, ?, ?)
        """.trimIndent()
    }
}

