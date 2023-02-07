package no.nav.services

import no.nav.database.dao.ConsentDao
import no.nav.models.Consent
import no.nav.models.CreateConsentRequest

class ConsentService(
    private val consentDao: ConsentDao
) {
    fun createConsent(createConsentRequest: CreateConsentRequest, employeeId: String) {
        //TODO: handle database saying: "consent code already exists" exception
        consentDao.createConsent(createConsentRequest, employeeId, createConsentCode())
    }

    fun getActiveConsents(employeeId: String): List<Consent> = consentDao.getActiveConsents(employeeId)

    private fun createConsentCode(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..7).map {
            allowedChars.random()
        }.joinToString("").toCharArray().also {
            it[3] = '-'
        }.joinToString("")
    }
}