package no.nav

import no.nav.consent.ConsentDao
import no.nav.consent.ConsentService
import no.nav.database.DataSourceBuilder
import no.nav.database.dao.*
import no.nav.services.*

class ApplicationContext(private val env: Map<String, String>) {
    val employeeService: EmployeeService
    val citizenService: CitizenService
    val consentService: ConsentService
    val messageService: MessageService
    val candidateService: CandidateService

    init {
        val dataSourceBuilder = DataSourceBuilder(System.getenv())
        dataSourceBuilder.migrate()
        val dataSource = dataSourceBuilder.dataSource

        val employeeDao = EmployeeDao(dataSource)
        val citizenDao = CitizenDao(dataSource)
        val consentDao = ConsentDao(dataSource)
        val candidateDao = CandidateDao(dataSource)
        val messageDao = MessageDao(dataSource)

        messageService = MessageService(messageDao)
        employeeService = EmployeeService(employeeDao)
        citizenService = CitizenService(citizenDao)
        consentService = ConsentService(consentDao, candidateDao, employeeDao, messageService)
        candidateService = CandidateService(consentDao, candidateDao, messageService)
    }
}