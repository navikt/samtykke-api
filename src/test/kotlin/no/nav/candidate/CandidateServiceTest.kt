package no.nav.candidate

import io.ktor.server.plugins.*
import io.mockk.*
import kotlinx.datetime.LocalDate
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.models.CandidateStatus
import no.nav.models.CreateCandidateRequest
import no.nav.services.CandidateService
import no.nav.services.MessageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class CandidateServiceTest {

    private val consentDao = mockk<ConsentDao>()
    private val candidateDao = mockk<CandidateDao>()
    private val messageService = mockk<MessageService>(relaxed = true)

    private val candidateService = CandidateService(consentDao, candidateDao, messageService)

    @AfterEach
    fun afterEach() {
        confirmVerified(candidateDao)
        clearAllMocks()
    }

    @Test
    fun `create valid candidate`() {
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
}