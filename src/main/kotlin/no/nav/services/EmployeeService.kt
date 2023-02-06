package no.nav.services

import no.nav.database.dao.EmployeeDao

class EmployeeService(
    private val employeeDao: EmployeeDao
) {
    fun getEmployee(employeeId: String) = employeeDao.getEmployee(employeeId)
}