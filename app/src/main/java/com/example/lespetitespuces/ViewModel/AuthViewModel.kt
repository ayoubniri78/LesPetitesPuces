// fichier: com/example/lespetitespuces/viewmodel/AuthViewModel.kt
package com.example.lespetitespuces.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest


// AuthResult reste le même
sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
    object Loading : AuthResult()
}

// Nouveau sealed class pour le résultat de la réinitialisation du mot de passe
sealed class PasswordResetResult {
    object Success : PasswordResetResult()
    data class Error(val exception: Exception) : PasswordResetResult()
    object Loading : PasswordResetResult() // Optionnel
    object Idle : PasswordResetResult() // État initial ou après une opération
}


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    // LiveData pour le résultat de la réinitialisation du mot de passe
    private val _passwordResetResult = MutableLiveData<PasswordResetResult>(PasswordResetResult.Idle)
    val passwordResetResult: LiveData<PasswordResetResult> = _passwordResetResult
    private val _profileUpdateResult = MutableLiveData<AuthResult>() // Réutiliser AuthResult pour simplicité
    val profileUpdateResult: LiveData<AuthResult> = _profileUpdateResult

    init {
        _currentUser.value = firebaseAuth.currentUser
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val authResultFirebase = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = authResultFirebase.user
                _authResult.value = AuthResult.Success(authResultFirebase.user)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e)
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val authResultFirebase = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                _currentUser.value = authResultFirebase.user
                _authResult.value = AuthResult.Success(authResultFirebase.user)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e)
            }
        }
    }

    fun logoutUser() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Nouvelle fonction pour envoyer l'e-mail de réinitialisation
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _passwordResetResult.value = PasswordResetResult.Error(IllegalArgumentException("L'e-mail ne peut pas être vide."))
            return
        }
        viewModelScope.launch {
            _passwordResetResult.value = PasswordResetResult.Loading
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                _passwordResetResult.value = PasswordResetResult.Success
            } catch (e: Exception) {
                _passwordResetResult.value = PasswordResetResult.Error(e)
            }
        }
    }

    // Fonction pour réinitialiser l'état du résultat de la réinitialisation du mot de passe
    fun resetPasswordResetState() {
        _passwordResetResult.value = PasswordResetResult.Idle
    }
    fun updateUserDisplayName(newName: String) {
        val user = firebaseAuth.currentUser
        if (user == null || newName.isBlank()) {
            _profileUpdateResult.value = AuthResult.Error(IllegalArgumentException("Utilisateur non connecté ou nom invalide."))
            return
        }
        viewModelScope.launch {
            _profileUpdateResult.value = AuthResult.Loading
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            try {
                user.updateProfile(profileUpdates).await()
                // Rafraîchir _currentUser pour refléter le changement
                _currentUser.value = firebaseAuth.currentUser
                _profileUpdateResult.value = AuthResult.Success(firebaseAuth.currentUser)
            } catch (e: Exception) {
                _profileUpdateResult.value = AuthResult.Error(e)
            }
        }
    }

    fun updateUserEmail(newEmail: String, currentPasswordForReauth: String) {
        val user = firebaseAuth.currentUser
        if (user == null || newEmail.isBlank() || currentPasswordForReauth.isBlank()) {
            _profileUpdateResult.value = AuthResult.Error(IllegalArgumentException("Champs invalides ou utilisateur non connecté."))
            return
        }
        viewModelScope.launch {
            _profileUpdateResult.value = AuthResult.Loading
            try {
                // La ré-authentification est souvent nécessaire pour les opérations sensibles
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPasswordForReauth)
                user.reauthenticate(credential).await()

                // Mettre à jour l'e-mail
                user.updateEmail(newEmail).await()
                // Rafraîchir _currentUser
                _currentUser.value = firebaseAuth.currentUser
                _profileUpdateResult.value = AuthResult.Success(firebaseAuth.currentUser)
            } catch (e: Exception) {
                _profileUpdateResult.value = AuthResult.Error(e)
            }
        }
    }

    fun updateUserPassword(newPassword: String, currentPasswordForReauth: String) {
        val user = firebaseAuth.currentUser
        if (user == null || newPassword.isBlank() || currentPasswordForReauth.isBlank()) {
            _profileUpdateResult.value = AuthResult.Error(IllegalArgumentException("Champs invalides ou utilisateur non connecté."))
            return
        }
        if (newPassword.length < 6) {
            _profileUpdateResult.value = AuthResult.Error(IllegalArgumentException("Le nouveau mot de passe doit comporter au moins 6 caractères."))
            return
        }
        viewModelScope.launch {
            _profileUpdateResult.value = AuthResult.Loading
            try {
                // Ré-authentification
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPasswordForReauth)
                user.reauthenticate(credential).await()

                // Mettre à jour le mot de passe
                user.updatePassword(newPassword).await()
                _profileUpdateResult.value = AuthResult.Success(user) // Pas besoin de rafraîchir currentUser pour le mdp
            } catch (e: Exception) {
                _profileUpdateResult.value = AuthResult.Error(e)
            }
        }
    }
    fun resetProfileUpdateResult() {
        _profileUpdateResult.value = AuthResult.Success(null) // Ou un état Idle dédié
    }
}