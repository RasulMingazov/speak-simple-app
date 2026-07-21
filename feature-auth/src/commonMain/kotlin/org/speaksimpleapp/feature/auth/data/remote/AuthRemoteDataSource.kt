package org.speaksimpleapp.feature.auth.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import org.speaksimpleapp.feature.auth.data.remote.entity.AuthSessionResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.CurrentUserResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.GoogleAuthChallengeResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.GoogleLoginRequest
import org.speaksimpleapp.feature.auth.data.remote.entity.RefreshTokenRequest

internal interface AuthRemoteDataSource {
    suspend fun createChallenge(): GoogleAuthChallengeResponse
    suspend fun login(request: GoogleLoginRequest): AuthSessionResponse
    suspend fun refresh(refreshToken: String): AuthSessionResponse
    suspend fun logout(refreshToken: String)
    suspend fun getCurrentUser(accessToken: String): CurrentUserResponse
    suspend fun deleteAccount(accessToken: String)
}

internal class KtorAuthRemoteDataSource(
    baseUrl: String,
    engine: HttpClientEngine,
) : AuthRemoteDataSource {
    private val baseUrl = baseUrl.trimEnd('/')
    private val client = HttpClient(engine) {
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            socketTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; explicitNulls = false })
        }
    }

    override suspend fun createChallenge(): GoogleAuthChallengeResponse = execute {
        client.post("$baseUrl/api/v1/auth/google/challenge").requireSuccess().body()
    }

    override suspend fun login(request: GoogleLoginRequest): AuthSessionResponse = execute {
        client.post("$baseUrl/api/v1/auth/google/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.requireSuccess().body()
    }

    override suspend fun refresh(refreshToken: String): AuthSessionResponse = execute {
        client.post("$baseUrl/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }.requireSuccess().body()
    }

    override suspend fun logout(refreshToken: String) = execute {
        client.post("$baseUrl/api/v1/auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }.requireSuccess()
        Unit
    }

    override suspend fun getCurrentUser(accessToken: String): CurrentUserResponse = execute {
        val response = client.get("$baseUrl/api/v1/users/me") {
            bearerAuth(accessToken)
        }
        when {
            response.status.isSuccess() -> CurrentUserResponse.Success(response.body())
            response.status == HttpStatusCode.Unauthorized -> CurrentUserResponse.Unauthorized
            else -> throw AuthDataException()
        }
    }

    override suspend fun deleteAccount(accessToken: String) = execute {
        client.delete("$baseUrl/api/v1/users/me") {
            bearerAuth(accessToken)
        }.requireSuccess()
        Unit
    }

    private fun HttpResponse.requireSuccess(): HttpResponse = apply {
        if (!status.isSuccess()) throw AuthDataException()
    }

    private suspend fun <T> execute(block: suspend () -> T): T = try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: AuthDataException) {
        throw error
    } catch (error: Throwable) {
        throw AuthDataException(error)
    }

    private companion object {
        const val REQUEST_TIMEOUT_MILLIS = 15_000L
        const val CONNECT_TIMEOUT_MILLIS = 10_000L
    }
}

internal expect fun platformHttpClientEngine(): HttpClientEngine
