package no.nav.database.dao

import kotlinx.datetime.LocalDate
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_ALL_CANDIDATES_BY_CONSENT
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_ALL_CANDIDATURES_BY_CITIZEN
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_CANDIDATE
import no.nav.database.toList
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import javax.sql.DataSource

class CandidateDao(
    private val dataSource: DataSource
) {
    fun getCandidatesByConsentId(consentId: Long): List<Candidate> {
        dataSource.connection.use {
            return it.prepareStatement(SELECT_ALL_CANDIDATES_BY_CONSENT).apply {
                setLong(1, consentId)
            }.executeQuery().toList {
                Candidate(
                    getLong("id"),
                    getString("name"),
                    getString("email"),
                    CandidateStatus.valueOf(getString("status")),
                    LocalDate(
                        getDate("consented").toLocalDate().year,
                        getDate("consented").toLocalDate().month,
                        getDate("consented").toLocalDate().dayOfMonth
                    ),
                    getBoolean("audio_recording"),
                    getBoolean("store_info"),
                    null
                )
            }
        }
    }

    fun getCitizenCandidaturesByCitizenId(citizenId: String): List<Candidate> {
        dataSource.connection.use {
            return it.prepareStatement(SELECT_ALL_CANDIDATURES_BY_CITIZEN).apply {
                setString(1, citizenId)
            }.executeQuery().toList {
                Candidate(
                    getLong("id"),
                    getString("name"),
                    getString("email"),
                    CandidateStatus.valueOf(getString("status")),
                    LocalDate(
                        getDate("consented").toLocalDate().year,
                        getDate("consented").toLocalDate().month,
                        getDate("consented").toLocalDate().dayOfMonth
                    ),
                    getBoolean("audio_recording"),
                    getBoolean("store_info"),
                    getLong("consent_id")
                )
            }
        }
    }

    fun getCitizenCandidature(consentId: Long, citizenId: String): Candidate {
        dataSource.connection.use {
            val result =  it.prepareStatement(SELECT_CANDIDATE).apply {
                setLong(1, consentId)
                setString(2, citizenId)
            }.executeQuery()
            return if (result.next()) {
                Candidate(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getString("email"),
                    CandidateStatus.valueOf(result.getString("status")),
                    LocalDate(
                        result.getDate("consented").toLocalDate().year,
                        result.getDate("consented").toLocalDate().month,
                        result.getDate("consented").toLocalDate().dayOfMonth
                    ),
                    result.getBoolean("audio_recording"),
                    result.getBoolean("store_info"),
                    result.getLong("consent_id")
                )
            } else {
                throw Exception("Could not find candidate with $consentId and $citizenId")
            }
        }
    }

    private object CandidateQueries {
        val SELECT_ALL_CANDIDATES_BY_CONSENT = """
            SELECT * FROM candidate
            WHERE consent_id = ?
        """.trimIndent()

        val SELECT_ALL_CANDIDATURES_BY_CITIZEN = """
            SELECT * FROM candidate
            WHERE citizen_id = ?
        """.trimIndent()

        val SELECT_CANDIDATE = """
            SELECT * FROM candidate
            WHERE consent_id = ? AND citizen_id = ?
        """.trimIndent()
    }
}