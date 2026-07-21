package org.speaksimpleapp.feature.auth.data.identity

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import org.speaksimpleapp.feature.auth.data.platform.AndroidActivityProvider

internal class AndroidGoogleIdentityProvider(
    private val activityProvider: AndroidActivityProvider,
    private val serverClientId: String,
) : GoogleIdentityProvider {
    override suspend fun signIn(nonce: String): GoogleIdToken? {
        if (serverClientId.isBlank()) throw AuthDataException()
        val activity = activityProvider.requireActivity()
        val credentialManager = CredentialManager.create(activity)
        val option = GetSignInWithGoogleOption.Builder(serverClientId)
            .setNonce(nonce)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        return try {
            val credential = credentialManager.getCredential(activity, request).credential
            if (credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) throw AuthDataException()
            GoogleIdToken(
                GoogleIdTokenCredential.createFrom(credential.data).idToken,
            )
        } catch (_: GetCredentialCancellationException) {
            null
        } catch (error: CancellationException) {
            throw error
        } catch (error: AuthDataException) {
            throw error
        } catch (error: Throwable) {
            throw AuthDataException(error)
        }
    }

    override suspend fun signOut() = Unit
}
