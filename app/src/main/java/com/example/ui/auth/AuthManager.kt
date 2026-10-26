package com.example.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.ui.util.AppLanguage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AuthManager {

    private const val FALLBACK_WEB_CLIENT_ID = "364598570519-tk6c64o249012cg9jjodlasoc60roase.apps.googleusercontent.com"

    private fun Context.findActivity(): Activity? {
        var current = this
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }

    private fun resolveWebClientId(context: Context): String {
        return try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) {
                context.getString(resId).ifBlank { FALLBACK_WEB_CLIENT_ID }
            } else {
                FALLBACK_WEB_CLIENT_ID
            }
        } catch (e: Exception) {
            FALLBACK_WEB_CLIENT_ID
        }
    }

    fun attemptAutoSignIn(
        context: Context,
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit,
        onUnauthenticated: () -> Unit,
        scope: CoroutineScope
    ) {
        if (Firebase.auth.currentUser != null) {
            onAuthSuccess()
            return
        }

        val clientId = resolveWebClientId(context)
        val activity = context.findActivity() ?: (context as? Activity)
        if (activity == null) {
            onUnauthenticated()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                    authResult.user?.let { user ->
                        syncUserProfileToFirestore(context, user.uid, user.displayName, user.photoUrl?.toString())
                    }
                    onAuthSuccess()
                } else {
                    onUnauthenticated()
                }
            } catch (e: Exception) {
                onUnauthenticated()
            }
        }
    }

    fun onGoogleSignInClicked(
        context: Context,
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit,
        onAuthError: (String) -> Unit,
        scope: CoroutineScope,
        onAuthCancelled: () -> Unit = {}
    ) {
        val clientId = resolveWebClientId(context)
        val activity = context.findActivity() ?: (context as? Activity)

        if (activity == null) {
            val error = if (AppLanguage.isDeviceArabic()) "تعذر فتح نافذة تسجيل الدخول" else "Activity context not found"
            onAuthError(error)
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                    authResult.user?.let { user ->
                        syncUserProfileToFirestore(context, user.uid, user.displayName, user.photoUrl?.toString())
                    }
                    onAuthSuccess()
                } else {
                    onAuthError(if (AppLanguage.isDeviceArabic()) "نوع بيانات الاعتماد غير مدعوم" else "Unsupported credential type")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("Auth", "User dismissed or cancelled Google Sign-In")
                onAuthCancelled()
            } catch (e: NoCredentialException) {
                Log.w("Auth", "No Google credentials available on device", e)
                val msg = if (AppLanguage.isDeviceArabic()) {
                    "لم يتم العثور على حساب Google مسجل في إعدادات الهاتف. يرجى إضافة حسابك من إعدادات النظام."
                } else {
                    "No Google accounts found in device settings. Please add an account to your device."
                }
                onAuthError(msg)
            } catch (e: Exception) {
                Log.e("Auth", "Google Sign-In failed", e)
                val msg = if (AppLanguage.isDeviceArabic()) {
                    "تعذر إكمال تسجيل الدخول: ${e.localizedMessage ?: "حدث خطأ غير متوقع"}"
                } else {
                    e.localizedMessage ?: "Google Sign-In failed"
                }
                onAuthError(msg)
            }
        }
    }

    private suspend fun syncUserProfileToFirestore(
        context: Context,
        userId: String,
        displayName: String?,
        photoUrl: String?
    ) {
        try {
            val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            val dbId = if (resId != 0) context.getString(resId) else "ai-studio-android-aftergam-99d9c27b-8194-493d-91a2-0b303f5127cc"
            val db = FirebaseFirestore.getInstance(dbId)
            val userRef = db.collection("users").document(userId)

            val profile = hashMapOf<String, Any>(
                "userId" to userId,
                "username" to (displayName?.ifBlank { "Gamer" } ?: "Gamer"),
                "displayName" to (displayName ?: ""),
                "photoUrl" to (photoUrl ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
            userRef.set(profile, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("Auth", "User profile sync to Firestore skipped: ${e.message}")
        }
    }

    fun signOut(
        context: Context,
        credentialManager: CredentialManager,
        onSignOutComplete: () -> Unit,
        scope: CoroutineScope
    ) {
        Firebase.auth.signOut()
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("Auth", "Failed to clear credential state", e)
            } finally {
                onSignOutComplete()
            }
        }
    }
}
