package no.nav.services

import io.ktor.server.plugins.*
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.models.Candidate
import no.nav.models.Consent
import no.nav.models.BaseConsent

class ConsentService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao,
    private val employeeDao: EmployeeDao
) {
    fun createConsent(baseConsent: BaseConsent, employeeId: String) {
        try {
            validateConsent(baseConsent)
        } catch (e: Exception) {
            throw BadRequestException("Consent not valid")
        }

        var unique: Boolean
        do {
            val code = createConsentCode()
            try {
                consentDao.getConsentByCode(code)
                break
            } catch (e: Exception) {
                if (e is NotFoundException) consentDao.createConsent(baseConsent, employeeId, code)
                unique = true
            }
        } while (!unique)
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
            consents.add(consentDao.getConsentById(it.consentId!!))
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
            consent.code,
            candidates,
            employee
        )
    }

    fun deleteExpiredConsentsAndConnectedCandidates() {
        // Issues with cascading not deleting candidates, have to do it manually
        // If deleting candidates fails => not finding any candidates to delete,
        // Do delte consents
        try {
            candidateDao.deleteCandidatesFromExpiredConsents()
            consentDao.deleteExpiredConsents()
        } catch (e: Exception) {
            consentDao.deleteExpiredConsents()
        }
    }

    private fun validateConsent(baseConsent: BaseConsent) {
        require(baseConsent.title.length > 5) { "Title must be longer than 5 characters" }
        require(baseConsent.title.length < 50) { "Title must be shorter than 50 characters" }
        require(baseConsent.responsibleGroup.isNotBlank()) { "Responsible group must be set" }
        require(baseConsent.theme.isNotBlank()) { "Theme must be set" }
        require(baseConsent.purpose.length > 30) { "Purpose must be longer than 30 characters" }
        require(baseConsent.purpose.length < 300) { "Purpose must be shorten than 300 characters" }
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