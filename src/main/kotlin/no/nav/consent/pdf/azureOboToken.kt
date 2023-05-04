package no.nav.consent.pdf

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

fun getAzureOBOToken(): String = runBlocking {
    val accessToken = getAzureADToken()

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(accessToken, "")
                }
            }
        }
    }

    val tokenOptions =  Parameters.build {
        append("grant_type", "client_credentials")
        append("client_id", System.getenv("AZURE_APP_CLIENT_ID"))
        append("client_secret", System.getenv("AZURE_APP_CLIENT_SECRET"))
        append("assertion", accessToken)
        append("scope", "api://dev-gcp.team-researchops.samtykke-slackbot/.default")
        append("requested_token_use", "on_behalf_of")
    }.formUrlEncode()

    val response: AzureTokenResponseBody = httpClient.post{
        url("https://login.microsoftonline.com/${System.getenv("AZURE_APP_TENANT_ID")}/oauth2/v2.0/token")
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(tokenOptions)
    }.body()

    return@runBlocking response.access_token
}

private fun getAzureADToken(): String = runBlocking {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    val tokenOptions = Parameters.build {
        append("client_id", System.getenv("AZURE_APP_CLIENT_ID"))
        append("grant_type", "client_credentials")
        append("scope", "api://dev-gcp.team-researchops.samtykke-slackbot/.default")
        append("client_secret", System.getenv("AZURE_APP_CLIENT_SECRET"))
    }.formUrlEncode()

    val response: AzureTokenResponseBody = httpClient.post {
        url("https://login.microsoftonline.com/${System.getenv("AZURE_APP_TENANT_ID")}/oauth2/v2.0/token")
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(tokenOptions)
    }.body()

    return@runBlocking response.access_token
}

// Variable names have to be like this due to incoming azure response
@Serializable
data class AzureTokenResponseBody(
    val token_type: String,
    val expires_in: Long,
    val ext_expires_in: Long,
    val access_token: String
)