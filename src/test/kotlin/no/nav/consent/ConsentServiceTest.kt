package no.nav.consent

import io.mockk.*
import kotlinx.datetime.LocalDate
import no.nav.createRandomString
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.models.BaseConsent
import no.nav.models.Candidate
import no.nav.models.Consent
import no.nav.services.ConsentService
import no.nav.services.MessageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class ConsentServiceTest {

    private val consentDao = mockk<ConsentDao>()
    private val candidateDao = mockk<CandidateDao>()
    private val employeeDao = mockk<EmployeeDao>()

    private val consentService = ConsentService(consentDao, candidateDao, employeeDao, MessageService(mockk()))

    @AfterEach
    fun afterEach() {
        confirmVerified(consentDao)
        clearAllMocks()
    }

    @Test
    fun `create valid consent`() {
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

        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
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

    @Test
    fun `get full consent by consent code`() {
        every { consentDao.getConsentByCode(any()) }.returns(
            Consent(
                1,
                "Brukertest av den nye samtykkeløsningen",
                "Team ResearchOps",
                "Samtykke",
                "Formålet med brukertesten er å finne ut om det nye digitale samtykkeskjemaet til NAV fungerer som det skal",
                3,
                LocalDate(2023, 6, 12),
                "rapport",
                "",
                "XX1-XX2",
                listOf(),
                null
            )
        )

        consentService.getConsentByCode("XX1-XX2")

        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
    }

    @Test
    fun `employee is able to retrieve active consents`() {
        every { consentDao.getActiveConsents(any()) }.returns(listOf(mockk(), mockk(), mockk()))

        val activeConsents = consentService.getEmployeeActiveConsents("")

        assertEquals(activeConsents.size, 3)

        verify(exactly = 1) { consentDao.getActiveConsents(any()) }
    }

    @Test
    fun `citizen is able to retrieve active consents`() {
        every { candidateDao.getCitizenCandidaturesByCitizenId(any()) }.returns(listOf(
            mockk { every { consentId }.returns(2) },
            mockk { every { consentId }.returns(2) }
        ))
        every { consentDao.getConsentById(any()) }.returns(mockk())

        val activeConsent = consentService.getCitizenActiveConsents("")

        assertEquals(activeConsent.size, 2)

        verify(exactly = 2) { consentDao.getConsentById(any()) }
    }

    @Test
    fun `error thrown when employee have no active consents`() {
        every { consentDao.getActiveConsents(any()) }.returns(listOf())

        assertFails {
            consentService.getEmployeeActiveConsents("")
        }

        verify(exactly = 1) { consentDao.getActiveConsents(any()) }
    }

    @Test
    fun `error thrown when citizen have no active consents`() {
        every { candidateDao.getCitizenCandidaturesByCitizenId(any())}.returns(listOf())

        assertFails {
            consentService.getCitizenActiveConsents("")
        }

        verify(exactly = 0) { consentDao.getConsentById(any()) }
    }
}