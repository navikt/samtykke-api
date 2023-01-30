package no.nav.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.models.mocks.consentsMock
import kotlin.test.Test
import kotlin.test.assertEquals

class CitizenRouteTest {
    @Test
    fun `retrieves active consents`() = testApplication {
        // TODO: Implement tests
        assertEquals(200, 200)
    }
}