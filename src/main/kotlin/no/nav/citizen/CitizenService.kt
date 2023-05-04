package no.nav.citizen

import io.ktor.server.plugins.*

class CitizenService(
    private val citizenDao: CitizenDao
) {
    fun createIfNotExists(citizenId: String) {
        try {
            getCitizen(citizenId)
        } catch (e: Exception) {
            if (e is NotFoundException) createCitizen(citizenId)
        }
    }

    private fun getCitizen(citizenId: String) = citizenDao.getCitizen(citizenId)

    private fun createCitizen(citizenId: String) = citizenDao.createCitizen(citizenId)

}