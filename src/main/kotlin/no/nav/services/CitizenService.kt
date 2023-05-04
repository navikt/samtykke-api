package no.nav.services

import io.ktor.server.plugins.*
import no.nav.citizen.CitizenDao

class CitizenService(
    private val citizenDao: CitizenDao
) {
    fun getCitizen(citizenId: String) = citizenDao.getCitizen(citizenId)

    fun createCitizen(citizenId: String) = citizenDao.createCitizen(citizenId)

    fun createIfNotExists(citizenId: String) {
        try {
            getCitizen(citizenId)
        } catch (e: Exception) {
            if (e is NotFoundException) createCitizen(citizenId)
        }
    }
}