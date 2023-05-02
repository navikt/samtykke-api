package no.nav.consent

import io.mockk.*
import kotlinx.datetime.LocalDate
import no.nav.createRandomString
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.models.BaseConsent
import no.nav.services.ConsentService
import no.nav.services.MessageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertFails

internal class ConsentServiceTest {

    private val consentDao = mockk<ConsentDao>()
    private val candidateDao = mockk<CandidateDao>()
    private val employeeDao = mockk<EmployeeDao>()

    private val consentService = ConsentService(consentDao, candidateDao, employeeDao, MessageService(mockk()))

    @AfterEach
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun `create valid consent`() {
        justRun { consentDao.createConsent(any(), any(), any()) }

        val validConsent = BaseConsent(
            "Brukertest av den nye samtykkeløsningen",
            "Team ResearchOps",
            "Samtykke",
            "Formålet med brukertesten er å finne ut om det nye digitale samtykkeskjemaet til NAV fungerer som det skal",
            3,
            LocalDate(2023, 6, 12),
            "rapport",
            ""
        )

        assertDoesNotThrow {
            consentService.createConsent(validConsent, "")
        }
    }

    @Test
    fun `fails to create in-valid consent`() {
        val emptyConsent = BaseConsent(
            "",
            "",
            "",
            "",
            0,
            LocalDate(2023, 6, 12),
            "",
            ""
        )

        val overFilledConsent = BaseConsent(
            createRandomString(70),
            createRandomString(8),
            createRandomString(10),
            createRandomString(500),
            3,
            LocalDate(2023, 6, 12),
            createRandomString(9),
            ""
        )

        assertFails {
            consentService.createConsent(emptyConsent, "")
            consentService.createConsent(overFilledConsent, "")
        }
    }
}