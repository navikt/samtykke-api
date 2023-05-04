package no.nav.candidate

import io.ktor.server.plugins.*
import io.mockk.*
import kotlinx.datetime.LocalDate
import no.nav.database.dao.CandidateDao
import no.nav.consent.ConsentDao
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import no.nav.models.CreateCandidateRequest
import no.nav.services.CandidateService
import no.nav.services.MessageService
import org.junit.jupiter.api.*

internal class CandidateServiceTest {

    private val consentDao = mockk<ConsentDao>()
    private val candidateDao = mockk<CandidateDao>()
    private val messageService = mockk<MessageService>(relaxed = true)

    private val candidateService = CandidateService(consentDao, candidateDao, messageService)

    @BeforeEach
    fun beforeEach() {
        every { consentDao.getOwnerIdByConsentId(any()) }.returns("")
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
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(candidateDao)
        clearAllMocks()
    }

    @Test
    fun `create valid candidate`() {
        every { candidateDao.createCandidature(any(), any(), any()) }.returns(mockk())

        val validCandidate = CreateCandidateRequest(
            "Pølle Bølle",
            "pølle.bølle@outlook.com",
            CandidateStatus.ACCEPTED,
            LocalDate(2023, 6, 12),
            false
        )

        assertDoesNotThrow {
            candidateService.createCandidature(validCandidate, "", "")
        }

        verify(exactly = 1) { candidateDao.createCandidature(any(), any(), any()) }
    }

    @Test
    fun `unable to create in-valid candidate`() {
        val invalidCandidate = CreateCandidateRequest(
            "",
            "",
            CandidateStatus.ACCEPTED,
            LocalDate(2023, 6, 12),
            false
        )

        assertThrows<BadRequestException> {
            candidateService.createCandidature(invalidCandidate, "", "")
        }

        verify(exactly = 0) { candidateDao.createCandidature(any(), any(), any()) }
    }

    @Test
    fun `able to anonymize candidate`() {
        every { candidateDao.anonymizeCandidate(any(), any()) }.returns(mockk())
        every { candidateDao.getCitizenCandidature(any(), any()) }.returns(
            mockk { every { trackingNumber }.returns("xxx") }
        )

        assertDoesNotThrow {
            candidateService.anonymizeCandidate("", "")
        }

        verify(exactly = 1) { candidateDao.getCitizenCandidature(any(), any()) }
        verify(exactly = 1) { candidateDao.anonymizeCandidate(any(), any()) }
    }

    @Test
    fun `able to update valid candidate`() {
        every { candidateDao.getCitizenCandidature(any(), any()) }.returns(
            mockk { every { trackingNumber }.returns("xxx") }
        )
        every { candidateDao.updateCandidate(any(), any(), any()) }.returns(mockk())

        val updatedCandidate = Candidate(
            1,
            "Nussi nøff",
            "nussi.noef@gmail.com",
            CandidateStatus.ACCEPTED,
            null,
            "xx",
            true,
            1
        )

        assertDoesNotThrow {
            candidateService.updateCandidate(updatedCandidate, "", "")
        }

        verify(exactly = 1) { candidateDao.getCitizenCandidature(any(), any()) }
        verify(exactly = 1) { candidateDao.updateCandidate(any(), any(), any()) }
    }

    @Test
    fun `unable to update in-valid candidate`() {
        every { candidateDao.getCitizenCandidature(any(), any()) }.returns(
            mockk { every { trackingNumber }.returns("xxx") }
        )

        val invalidUpdatedCandidate = Candidate(
            1,
            "",
            "",
            CandidateStatus.ACCEPTED,
            null,
            "xx",
            false,
            1
        )

        assertThrows<BadRequestException> {
            candidateService.updateCandidate(invalidUpdatedCandidate, "", "")
        }

        verify(exactly = 0) { candidateDao.getCitizenCandidature(any(), any()) }
        verify(exactly = 0) { candidateDao.updateCandidate(any(), any(), any()) }
    }
}