package no.nav.database.dao

import no.nav.database.dao.EmployeeDao.EmployeeQueries.SELECT_EMPLOYEE
import no.nav.models.Employee
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
                // TODO: add propper exception handling
                throw Exception("Employee not found")
            }
        }
    }

    private object EmployeeQueries {
        val SELECT_EMPLOYEE = """
            SELECT *
            FROM employee
            WHERE id = ?
        """.trimIndent()
    }
}

