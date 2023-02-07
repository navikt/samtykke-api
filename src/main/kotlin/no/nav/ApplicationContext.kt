package no.nav

import no.nav.database.DataSourceBuilder
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.database.dao.MessageDao
import no.nav.services.ConsentService
import no.nav.services.EmployeeService
import no.nav.services.MessageService

class ApplicationContext(private val env: Map<String, String>) {
    val employeeService: EmployeeService
    val consentService: ConsentService
    val messageService: MessageService

    init {
        val dataSourceBuilder = DataSourceBuilder(System.getenv())
        dataSourceBuilder.migrate()
        val dataSource = dataSourceBuilder.dataSource

        val employeeDao = EmployeeDao(dataSource)
        val consentDao = ConsentDao(dataSource)
        val candidateDao = CandidateDao(dataSource)
        val messageDao = MessageDao(dataSource)

        employeeService = EmployeeService(employeeDao)
        consentService = ConsentService(consentDao, candidateDao)
        messageService = MessageService(messageDao)
    }
}