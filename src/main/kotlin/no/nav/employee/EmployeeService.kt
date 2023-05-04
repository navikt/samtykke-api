package no.nav.employee

import io.ktor.server.plugins.*

class EmployeeService(
    private val employeeDao: EmployeeDao
) {
    fun getEmployee(employeeId: String) = employeeDao.getEmployee(employeeId)

    fun createIfNotExists(employee: Employee) {
        try {
            getEmployee(employee.id)
        } catch (e: Exception) {
            if (e is NotFoundException) createEmployee(employee)
        }
    }

    private fun createEmployee(employee: Employee) = employeeDao.createEmployee(employee)
}