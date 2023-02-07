package no.nav

import no.nav.database.DataSourceBuilder
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.services.ConsentService
import no.nav.services.EmployeeService

class ApplicationContext(private val env: Map<String, String>) {
    val employeeService: EmployeeService
    val consentService: ConsentService

    init {
        val dataSourceBuilder = DataSourceBuilder(System.getenv())
        dataSourceBuilder.migrate()
        val dataSource = dataSourceBuilder.dataSource

        val employeeDao = EmployeeDao(dataSource)
        val consentDao = ConsentDao(dataSource)

        employeeService = EmployeeService(employeeDao)
        consentService = ConsentService(consentDao)
    }
}