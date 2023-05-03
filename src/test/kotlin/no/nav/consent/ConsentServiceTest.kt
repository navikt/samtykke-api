package no.nav.consent

import io.ktor.server.plugins.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import no.nav.candidate.validateCandidateAnonymized
import no.nav.createRandomString
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.models.BaseConsent
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import no.nav.models.Consent
import no.nav.services.ConsentService
import no.nav.services.MessageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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
    fun `unable to create in-valid consent`() {
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

    @Test
    fun `get full consent with candidates`() {
        every { consentDao.getConsentByCode(any()) }.returns(
            mockk {
                every { id }.returns(1)
                every { title }.returns("Brukertest")
                every { responsibleGroup }.returns("Team ReOps")
                every { theme }.returns("skjemaer")
                every { purpose }.returns("teste veldig fine skjemaer")
                every { totalInvolved }.returns(2)
                every { expiration }.returns(LocalDate(2023, 6, 12))
                every { endResult }.returns("rapport")
                every { slackChannelId }.returns("")
                every { code }.returns("XX1-XX2")
                every { candidates }.returns(null)
                every { employee }.returns(null)
            }
        )
        every { candidateDao.getCandidatesByConsentId(any()) }.returns(listOf(mockk(), mockk(), mockk()))

        val consentWithCandidates = consentService.getConsentByCodeWithCandidates("")

        assertEquals(consentWithCandidates.candidates!!.size, 3)

        verify(exactly = 1) { candidateDao.getCandidatesByConsentId(any()) }
        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
    }

    @Test
    fun `able to get consent with empty list of candidates`() {
        every { consentDao.getConsentByCode(any()) }.returns(
            mockk {
                every { id }.returns(1)
                every { title }.returns("Brukertest")
                every { responsibleGroup }.returns("Team ReOps")
                every { theme }.returns("skjemaer")
                every { purpose }.returns("teste veldig fine skjemaer")
                every { totalInvolved }.returns(2)
                every { expiration }.returns(LocalDate(2023, 6, 12))
                every { endResult }.returns("rapport")
                every { slackChannelId }.returns("")
                every { code }.returns("XX1-XX2")
                every { candidates }.returns(null)
                every { employee }.returns(null)
            }
        )
        every { candidateDao.getCandidatesByConsentId(any()) }.returns(listOf())

        val consentWithCandidates = consentService.getConsentByCodeWithCandidates("")

        assertEquals(consentWithCandidates.candidates!!.size, 0)

        verify(exactly = 1) { candidateDao.getCandidatesByConsentId(any()) }
        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
    }
    
    @Test
    fun `able to get consent with mix of anonymized and un-anonymized candidates`() {
        every { consentDao.getConsentByCode(any()) }.returns(
            mockk {
                every { id }.returns(1)
                every { title }.returns("Brukertest")
                every { responsibleGroup }.returns("Team ReOps")
                every { theme }.returns("skjemaer")
                every { purpose }.returns("teste veldig fine skjemaer")
                every { totalInvolved }.returns(2)
                every { expiration }.returns(LocalDate(2023, 6, 12))
                every { endResult }.returns("rapport")
                every { slackChannelId }.returns("")
                every { code }.returns("XX1-XX2")
                every { candidates }.returns(null)
                every { employee }.returns(null)
            }
        )
        every { candidateDao.getCandidatesByConsentId(any()) }.returns(listOf(
            mockk(),
            mockk {
                every { name }.returns("")
                every { email }.returns("")
                every { status }.returns(CandidateStatus.WITHDRAWN)
                every { consented }.returns(null)
                every { audioRecording }.returns(false)
            },
            mockk(),
            mockk {
                every { name }.returns("")
                every { email }.returns("")
                every { status }.returns(CandidateStatus.WITHDRAWN)
                every { consented }.returns(null)
                every { audioRecording }.returns(false)
            },
        ))

        val consentWithMixedCandidates = consentService.getConsentByCodeWithCandidates("")

        assertEquals(consentWithMixedCandidates.candidates!!.size, 4)

        validateCandidateAnonymized(consentWithMixedCandidates.candidates!![1])
        validateCandidateAnonymized(consentWithMixedCandidates.candidates!![3])

        verify(exactly = 1) { candidateDao.getCandidatesByConsentId(any()) }
        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
    }

    @Test
    fun `able to get citizens given consent`() {
        every { consentDao.getConsentByCode(any()) }.returns(
            mockk {
                every { id }.returns(1)
                every { title }.returns("Brukertest")
                every { responsibleGroup }.returns("Team ReOps")
                every { theme }.returns("skjemaer")
                every { purpose }.returns("teste veldig fine skjemaer")
                every { totalInvolved }.returns(2)
                every { expiration }.returns(LocalDate(2023, 6, 12))
                every { endResult }.returns("rapport")
                every { slackChannelId }.returns("")
                every { code }.returns("XX1-XX2")
                every { candidates }.returns(null)
                every { employee }.returns(null)
            }
        )
        every { consentDao.getOwnerIdByConsentId(any()) }.returns("xxx")
        every { employeeDao.getEmployee(any()) }.returns(mockk())
        every { candidateDao.getCitizenCandidature(any(), any()) }.returns(mockk())

        val consentWithCandidate = consentService.getConsentByCodeWithCandidate("", "")

        assertEquals(consentWithCandidate.candidates!!.size, 1)

        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
        verify(exactly = 1) { consentDao.getOwnerIdByConsentId(any()) }
        verify(exactly = 1) { employeeDao.getEmployee(any()) }
        verify(exactly = 1) { candidateDao.getCitizenCandidature(any(), any()) }
    }

    @Test
    fun `able to get citizen consent with no candidature`() {
        every { consentDao.getConsentByCode(any()) }.returns(
            mockk {
                every { id }.returns(1)
                every { title }.returns("Brukertest")
                every { responsibleGroup }.returns("Team ReOps")
                every { theme }.returns("skjemaer")
                every { purpose }.returns("teste veldig fine skjemaer")
                every { totalInvolved }.returns(2)
                every { expiration }.returns(LocalDate(2023, 6, 12))
                every { endResult }.returns("rapport")
                every { slackChannelId }.returns("")
                every { code }.returns("XX1-XX2")
                every { candidates }.returns(null)
                every { employee }.returns(null)
            }
        )
        every { consentDao.getOwnerIdByConsentId(any()) }.returns("xxx")
        every { employeeDao.getEmployee(any()) }.returns(mockk())
        every { candidateDao.getCitizenCandidature(any(), any()) }.throws(NotFoundException())

        val consentWithCandidate = consentService.getConsentByCodeWithCandidate("", "")

        assertEquals(consentWithCandidate.candidates!!.size, 0)

        verify(exactly = 1) { consentDao.getConsentByCode(any()) }
        verify(exactly = 1) { consentDao.getOwnerIdByConsentId(any()) }
        verify(exactly = 1) { employeeDao.getEmployee(any()) }
        verify(exactly = 1) { candidateDao.getCitizenCandidature(any(), any()) }
    }

    @Test
    fun `deletes expired consents and connected candidates`() = runBlocking {
        every { consentDao.getExpiredConsent() }.returns(listOf())
        // These methods are of type void, but they still have to "Mock" an empty response as to not throw NotFoundError
        every { consentDao.deleteExpiredConsents() }.returns(mockk())
        every { candidateDao.deleteCandidatesFromExpiredConsents() }.returns(mockk())

        consentService.deleteExpiredConsentsAndConnectedCandidates()

        verify(exactly = 1) { candidateDao.deleteCandidatesFromExpiredConsents() }
        verify(exactly = 1) { consentDao.deleteExpiredConsents() }
        verify(exactly = 1) { consentDao.getExpiredConsent() }
    }

    @Test
    fun `deletes expired consents with no connected candidates`() = runBlocking {
        every { consentDao.getExpiredConsent() }.returns(listOf())
        // THis method is of type void, but still need to "Mock" an empty response as to not throw NotFoundError
        every { consentDao.deleteExpiredConsents() }.returns(mockk())
        every { candidateDao.deleteCandidatesFromExpiredConsents() }.throws(NotFoundException())

        consentService.deleteExpiredConsentsAndConnectedCandidates()

        verify(exactly = 1) { candidateDao.deleteCandidatesFromExpiredConsents() }
        verify(exactly = 1) { consentDao.deleteExpiredConsents() }
        verify(exactly = 1) { consentDao.getExpiredConsent() }
    }
}