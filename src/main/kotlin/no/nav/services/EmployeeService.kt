package no.nav.services

import io.ktor.server.plugins.*
import no.nav.database.dao.EmployeeDao
import no.nav.models.Employee

class EmployeeService(
    private val employeeDao: EmployeeDao
) {
    fun getEmployee(employeeId: String) = employeeDao.getEmployee(employeeId)

    fun createEmployee(employee: Employee) = employeeDao.createEmployee(employee)

    fun createIfNotExists(employee: Employee) {
        try {
            getEmployee(employee.id)
        } catch (e: Exception) {
            if (e is NotFoundException) createEmployee(employee)
        }
    }
}