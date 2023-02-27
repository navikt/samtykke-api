package no.nav.services

import io.ktor.server.plugins.*
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.database.dao.EmployeeDao
import no.nav.models.Candidate
import no.nav.models.Consent
import no.nav.models.CreateConsentRequest

class ConsentService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao,
    private val employeeDao: EmployeeDao
) {
    fun createConsent(createConsentRequest: CreateConsentRequest, employeeId: String) {
        try {
            validateConsent(createConsentRequest)
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
                if (e is NotFoundException) consentDao.createConsent(createConsentRequest, employeeId, code)
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
            consent.purpose,
            consent.totalInvolved,
            consent.expiration,
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
            consent.purpose,
            consent.totalInvolved,
            consent.expiration,
            consent.code,
            candidates,
            employee
        )
    }

    private fun validateConsent(createConsentRequest: CreateConsentRequest) {
        require(createConsentRequest.title.length > 5) { "Title must be longer than 5 characters" }
        require(createConsentRequest.title.length < 50) { "Title must be shorter than 50 characters" }
        require(createConsentRequest.responsibleGroup.isNotBlank()) { "Responsible group must be set" }
        require(createConsentRequest.purpose.length > 30) { "Purpose must be longer than 30 characters" }
        require(createConsentRequest.purpose.length < 300) { "Purpose must be shorten than 300 characters" }
        require(createConsentRequest.totalInvolved > 0) { "There has to be at least 1 involved" }
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