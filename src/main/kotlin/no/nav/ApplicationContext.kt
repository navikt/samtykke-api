package no.nav

import no.nav.database.DataSourceBuilder
import no.nav.database.dao.EmployeeDao
import no.nav.services.EmployeeService

class ApplicationContext(private val env: Map<String, String>) {
    val employeeService: EmployeeService

    init {
        val dataSourceBuilder = DataSourceBuilder(System.getenv())
        dataSourceBuilder.migrate()
        val dataSource = dataSourceBuilder.dataSource

        val employeeDao = EmployeeDao(dataSource)

        employeeService = EmployeeService(employeeDao)
    }
}