package no.nav.services

import io.ktor.server.plugins.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import no.nav.models.CreateCandidateRequest
import no.nav.models.MessageType
import java.util.regex.Pattern

class CandidateService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao,
    private val messageService: MessageService
) {
    suspend fun createCandidature(createCandidateRequest: CreateCandidateRequest, code: String, citizenId: String) {
        val consent = consentDao.getConsentByCode(code)

        try {
            validateCandidate(createCandidateRequest)
        } catch (e: Exception) {
            throw BadRequestException("Candidate not valid")
        }

        candidateDao.createCandidature(createCandidateRequest, consent.id, citizenId)

        messageService.createMessage(
            MessageType.CITIZEN_ACCEPT_CONSENT,
            consent.title,
            code,
            "",
            listOf(),
            consentDao.getOwnerIdByConsentId(consent.id),
            consent.slackChannelId
        )
    }

    suspend fun anonymizeCandidate(code: String, citizenId: String) {
        val consent = consentDao.getConsentByCode(code)

        messageService.createMessage(
            MessageType.CITIZEN_WITHDRAW_CONSENT,
            consent.title,
            code,
            candidateDao.getCitizenCandidature(consent.id, citizenId).trackingNumber,
            listOf(),
            consentDao.getOwnerIdByConsentId(consent.id),
            consent.slackChannelId
        )

        candidateDao.anonymizeCandidate(consent.id, citizenId)
    }

    suspend fun updateCandidate(candidate: Candidate, code: String, citizenId: String) {
        val consent = consentDao.getConsentByCode(code)

        try {
            validateCandidate(candidate)
        } catch (e: Exception) {
            throw BadRequestException("Candidate not valid")
        }

        candidateDao.updateCandidate(consent.id, citizenId, candidate)

        messageService.createMessage(
            MessageType.CITIZEN_UPDATE_CONSENT,
            consent.title,
            code,
            candidateDao.getCitizenCandidature(consent.id, citizenId).trackingNumber,
            listOf(),
            consentDao.getOwnerIdByConsentId(consent.id),
            consent.slackChannelId
        )
    }

    private fun validateCandidate(createCandidateRequest: CreateCandidateRequest) {
        require(createCandidateRequest.name.isNotBlank()) { "Name must be set" }
        require(isEmail(createCandidateRequest.email)) { "Email must be valid" }
        require(consentedDateValid(createCandidateRequest.consented)) { "Consented date not valid" }
    }

    private fun validateCandidate(candidate: Candidate) {
        require(candidate.name.isNotBlank()) { "Name must be set" }
        require(isEmail(candidate.email)) { "Email must be valid" }
    }

    private fun isEmail(email: String): Boolean {
        return Pattern
            .compile("^[a-z\\wæøåA-Z\\wæøå0-9.!#$%&'*+/=?^_`\\{|\\}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
            .matcher(email).matches()
    }

    private fun consentedDateValid(consented: LocalDate): Boolean {
        val date = consented.toJavaLocalDate()
        return !(date.isBefore(java.time.LocalDate.now().minusDays(1)) || date.isAfter(java.time.LocalDate.now().plusMonths(3)))
    }
}