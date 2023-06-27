package no.nav.consent

import io.ktor.server.plugins.*
import kotlinx.coroutines.runBlocking
import no.nav.candidate.Candidate
import no.nav.candidate.CandidateDao
import no.nav.employee.EmployeeDao
import no.nav.message.MessageType
import no.nav.message.MessageService

class ConsentService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao,
    private val employeeDao: EmployeeDao,
    private val messageService: MessageService
) {
    fun createConsent(baseConsent: BaseConsent, employeeId: String): String {
        var consentCodeUsed = ""

        try {
            validateConsent(baseConsent)
        } catch (e: Exception) {
            throw BadRequestException("Consent not valid")
        }

        // Make sure that the consent code being generated is not already in database
        var unique: Boolean
        do {
            val code = createConsentCode()
            try {
                consentDao.getConsentByCode(code)
                break
            } catch (e: Exception) {
                if (e is NotFoundException) {
                    consentCodeUsed = consentDao.createConsent(baseConsent, employeeId, code)
                }
                unique = true
            }
        } while (!unique)

        return consentCodeUsed
    }

    fun getConsentByCode(code: String): Consent = consentDao.getConsentByCode(code)

    fun getEmployeeActiveConsents(employeeId: String): List<Consent> {
        val consents: List<Consent> = consentDao.getActiveConsents(employeeId)

        if (consents.isEmpty()) throw NotFoundException()

        return consents
    }

    fun getCitizenActiveConsents(citizenId: String): List<Consent> {
        val consents = mutableListOf<Consent>()

        candidateDao.getCitizenCandidaturesByCitizenId(citizenId).forEach {
            consents.add(consentDao.getConsentById(it.consentId))
        }

        if (consents.isEmpty()) throw NotFoundException()

        return consents
    }

    fun getConsentByCodeWithCandidates(code: String): Consent {
        val consent = consentDao.getConsentByCode(code)
        return Consent(
            consent.id,
            consent.title,
            consent.responsibleGroup,
            consent.theme,
            consent.purpose,
            consent.totalInvolved,
            consent.expiration,
            consent.endResult,
            consent.slackChannelId,
            consent.code,
            candidateDao.getCandidatesByConsentId(consent.id),
            null
        )
    }

    fun getConsentByCodeWithCandidate(code: String, citizenId: String): Consent {
        val consent = consentDao.getConsentByCode(code)
        val employeeId = consentDao.getOwnerIdByConsentId(consent.id)
        val employee = employeeDao.getEmployee(employeeId)

        val candidates = mutableListOf<Candidate>()
        try {
            candidates.add(candidateDao.getCitizenCandidature(consent.id, citizenId))
        } catch (e: Exception) {
            // If exception is NotFoundError, do nothing (empty candidates list is sent), else throw exception to be handled
            if (e !is NotFoundException) throw e
        }

        return Consent(
            consent.id,
            consent.title,
            consent.responsibleGroup,
            consent.theme,
            consent.purpose,
            consent.totalInvolved,
            consent.expiration,
            consent.endResult,
            consent.slackChannelId,
            consent.code,
            candidates,
            employee
        )
    }

    fun deleteExpiredConsentsAndConnectedCandidates() = runBlocking {
        val expiredConsents = consentDao.getExpiredConsent()

        expiredConsents.forEach {
            messageService.createMessage(
                MessageType.CONSENT_EXPIRE,
                it.title,
                it.code!!,
                "",
                stripCandidatesForTrackingNumber(candidateDao.getCandidatesByConsentId(it.id)),
                consentDao.getOwnerIdByConsentId(it.id),
                it.slackChannelId
            )
        }

        try {
            candidateDao.deleteCandidatesFromExpiredConsents()
            consentDao.deleteExpiredConsents()
        } catch (e: Exception) {
            consentDao.deleteExpiredConsents()
        }
    }

    private fun stripCandidatesForTrackingNumber(candidates: List<Candidate>): List<String> {
        val trackingNumbers = mutableListOf<String>()

        candidates.forEach {
            trackingNumbers.add(it.trackingNumber)
        }

        return trackingNumbers
    }

    private fun validateConsent(baseConsent: BaseConsent) {
        require(baseConsent.title.isNotBlank()) { "Title must be set" }
        require(baseConsent.responsibleGroup.isNotBlank()) { "Responsible group must be set" }
        require(baseConsent.theme.isNotBlank()) { "Theme must be set" }
        require(baseConsent.purpose.isNotBlank()) { "Purpose must be set" }
        require(baseConsent.totalInvolved > 0) { "There has to be at least 1 involved" }
        require(baseConsent.endResult.isNotBlank()) { "End result must be set" }
    }

    private fun createConsentCode(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..7).map {
            allowedChars.random()
        }.joinToString("").toCharArray().also {
            it[3] = '-'
        }.joinToString("")
    }
}