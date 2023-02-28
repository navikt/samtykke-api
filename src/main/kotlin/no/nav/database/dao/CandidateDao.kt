package no.nav.database.dao

import io.ktor.server.plugins.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import no.nav.database.dao.CandidateDao.CandidateQueries.POST_CANDIDATE
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_ALL_CANDIDATES_BY_CONSENT
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_ALL_CANDIDATURES_BY_CITIZEN
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_CANDIDATE
import no.nav.database.dao.CandidateDao.CandidateQueries.UPDATE_CANDIDATE
import no.nav.database.dao.CandidateDao.CandidateQueries.UPDATE_CANDIDATE_NO_CONSENTED_DATE
import no.nav.database.toList
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import no.nav.models.Citizen
import no.nav.models.CreateCandidateRequest
import java.sql.Date
import javax.sql.DataSource

class CandidateDao(
    private val dataSource: DataSource
) {
    // TODO: add filtering of anonymized candidatures
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

    // TODO: add filtering of anonymized candidatures
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
                throw NotFoundException()
            }
        }
    }

    fun createCandidature(candidate: CreateCandidateRequest, consentId: Long, citizenId: String) {
        try {
            dataSource.connection.use {
                it.prepareStatement(POST_CANDIDATE).apply {
                    setString(1, candidate.name)
                    setString(2, candidate.email)
                    setString(3, candidate.status.toString())
                    setDate(4, Date.valueOf(candidate.consented.toString()))
                    setBoolean(5, candidate.audioRecording)
                    setBoolean(6, candidate.storeInfo)
                    setLong(7, consentId)
                    setString(8, citizenId)
                }.executeUpdate()
            }
        } catch (e: Exception) {
            throw BadRequestException("Could not create candidate")
        }
    }

    fun anonymizeCandidate(consentId: Long, citizenId: String) {
        try {
            dataSource.connection.use {
                it.prepareStatement(UPDATE_CANDIDATE).apply {
                    setString(1, "")
                    setString(2, "")
                    setString(3, CandidateStatus.WITHDRAWN.toString())
                    setDate(4, null)
                    setBoolean(5, false)
                    setBoolean(6, false)
                    setString(7, null)
                    // Used to find correct candidate
                    setLong(8, consentId)
                    setString(9, citizenId)
                }.executeUpdate()
            }
        } catch (e: Exception) {
            throw BadRequestException("Could not anonymize")
        }
    }

    fun updateCandidate(consentId: Long, citizenId: String, newCandidate: Candidate) {
        try {
            dataSource.connection.use {
                it.prepareStatement(UPDATE_CANDIDATE_NO_CONSENTED_DATE).apply {
                    setString(1, newCandidate.name)
                    setString(2, newCandidate.email)
                    setString(3, newCandidate.status.toString())
                    setBoolean(4, newCandidate.audioRecording)
                    setBoolean(5, newCandidate.storeInfo)
                    // Used to find correct candidate
                    setLong(6, consentId)
                    setString(7, citizenId)
                }.executeUpdate()
            }
        } catch (e: Exception) {
            throw BadRequestException("Could not update candidate")
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

        val POST_CANDIDATE = """
            INSERT INTO candidate
            (name, email, status, consented, audio_recording, store_info, consent_id, citizen_id)
            VALUES
            (?, ?, ?::status, ?, ?, ?, ?, ?)
        """.trimIndent()

        // Should this return any value?
        val UPDATE_CANDIDATE = """
            UPDATE candidate
            SET name = ?, email = ?, status = ?::status, consented = ?, audio_recording = ?, store_info = ?, citizen_id = ?
            WHERE consent_id = ? AND citizen_id = ?
        """.trimIndent()

        val UPDATE_CANDIDATE_NO_CONSENTED_DATE = """
            UPDATE candidate
            SET name = ?, email = ?, status = ?::status, audio_recording = ?, store_info = ?
            WHERE consent_id = ? AND citizen_id = ?
        """.trimIndent()
    }
}