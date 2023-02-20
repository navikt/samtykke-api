package no.nav.services

import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.models.Consent
import no.nav.models.CreateConsentRequest

class ConsentService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao
) {
    fun createConsent(createConsentRequest: CreateConsentRequest, employeeId: String) {
        //TODO: handle database saying: "consent code already exists" exception
        consentDao.createConsent(createConsentRequest, employeeId, createConsentCode())
    }

    fun getConsentByCode(code: String): Consent = consentDao.getConsentByCode(code)

    fun getEmployeeActiveConsents(employeeId: String): List<Consent> = consentDao.getActiveConsents(employeeId)

    fun getCitizenActiveConsents(citizenId: String): List<Consent> {
        // 1. Find ALL canditatures by citizen id
        // 2. Get all consents connected to those canditatures

        val consents = mutableListOf<Consent>()

        candidateDao.getCitizenCandidaturesByCitizenId(citizenId).forEach {
            consents.add(consentDao.getConsentById(it.consentId!!))
        }

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
        val candidate = candidateDao.getCitizenCandidature(consent.id, citizenId)

        return Consent(
            consent.id,
            consent.title,
            consent.responsibleGroup,
            consent.purpose,
            consent.totalInvolved,
            consent.expiration,
            consent.code,
            listOf(candidate),
            null
        )
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