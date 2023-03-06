package no.nav.database.dao

import io.ktor.server.plugins.*
import no.nav.database.dao.CitizenDao.CitizenQueries.POST_CITIZEN
import no.nav.database.dao.CitizenDao.CitizenQueries.SELECT_CITIZEN
import no.nav.models.Citizen
import javax.sql.DataSource

class CitizenDao(
    private val dataSource: DataSource
) {
    fun getCitizen(citizenId: String): Citizen {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_CITIZEN).apply {
                setString(1, citizenId)
            }.executeQuery()
            return if (result.next()) {
                Citizen(
                    result.getString("id"),
                    listOf()
                )
            } else {
                throw NotFoundException()
            }
        }
    }

    fun createCitizen(citizenId: String) {
        dataSource.connection.use {
            it.prepareStatement(POST_CITIZEN).apply {
                setString(1, citizenId)
            }.executeUpdate()
        }
    }

    private object CitizenQueries {
        val SELECT_CITIZEN = """
            SELECT *
            FROM citizen
            WHERE id = ?
        """.trimIndent()

        val POST_CITIZEN = """
            INSERT INTO citizen
            (id)
            VALUES
            (?)
        """.trimIndent()
    }
}