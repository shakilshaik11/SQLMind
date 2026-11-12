package com.example.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserSession(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?,
    val isCloudSynced: Boolean,
    val isAnonymous: Boolean = false
)

object FirebaseAuthManager {
    private const val TAG = "FirebaseAuthManager"
    private var isFirebaseAvailable = false

    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession: StateFlow<UserSession?> = _userSession

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    /**
     * Initializes Firebase App safely.
     */
    fun initFirebase(context: Context) {
        try {
            FirebaseApp.initializeApp(context)
            isFirebaseAvailable = true
            Log.d(TAG, "Firebase initialized successfully.")
            
            val auth = getAuth()
            if (auth != null) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    _userSession.value = mapFirebaseUser(currentUser)
                }
            }
        } catch (e: Exception) {
            isFirebaseAvailable = false
            Log.w(TAG, "Firebase was not initialized (likely missing google-services.json details). Local state will be maintained.")
        }
    }

    fun isFirebaseInitialized(): Boolean = isFirebaseAvailable

    fun getAuth(): FirebaseAuth? {
        return if (isFirebaseAvailable) {
            try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun getFirestore(): FirebaseFirestore? {
        return if (isFirebaseAvailable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                null
            }
        } else null
    }

    private fun mapFirebaseUser(user: FirebaseUser): UserSession {
        return UserSession(
            uid = user.uid,
            displayName = user.displayName ?: "SQL Scholar",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString(),
            isCloudSynced = true
        )
    }

    /**
     * Simulation login to support elegant preview experience even before Firebase Console binding
     */
    fun performBackupLocalSession() {
        _userSession.value = UserSession(
            uid = "local_simulation_user_id",
            displayName = "Scholar Developer",
            email = "buujishaik877@gmail.com", // matches User email in request metadata! Beautifully custom!
            photoUrl = null,
            isCloudSynced = false,
            isAnonymous = true
        )
        _authError.value = "Local mock account created. (Note: Add google-services.json to unlock real Google Sign-In with Cloud sync)."
    }

    /**
     * Sign out clear session
     */
    fun signOut() {
        try {
            val auth = getAuth()
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Sign-out error on Firebase side")
        }
        _userSession.value = null
        _authError.value = null
    }

    /**
     * Integrates with real Firebase Credential login
     */
    fun handleFirebaseCredentialSignIn(idToken: String) {
        val auth = getAuth() ?: return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        _userSession.value = mapFirebaseUser(user)
                        _authError.value = null
                        Log.i(TAG, "Successfully authenticated with Firebase Auth via Google credential")
                    }
                } else {
                    val ex = task.exception
                    Log.e(TAG, "Firebase sign-in failed", ex)
                    _authError.value = ex?.localizedMessage ?: "Firebase login connection failed"
                }
            }
    }
}
