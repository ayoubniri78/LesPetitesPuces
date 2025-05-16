// fichier: com/example/lespetitespuces/ui/theme/ProfileScreen.kt (ou ui/screens/ProfileScreen.kt)
package com.example.lespetitespuces.ui.screens // Ou ui.theme, adaptez le package

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* // Utiliser Material 3
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed
import com.example.lespetitespuces.ui.theme.ItalianoFontFamily
// import com.example.lespetitespuces.ui.theme.White // Remplacer par Color.White si c'est juste ça
import com.example.lespetitespuces.viewmodel.AuthResult
import com.example.lespetitespuces.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel() // Obtenir le AuthViewModel
) {
    val currentUser by authViewModel.currentUser.observeAsState()
    val profileUpdateResult by authViewModel.profileUpdateResult.observeAsState()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var currentPasswordForReauth by remember { mutableStateOf("") } // Pour e-mail/mdp update
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    // Charger les données de l'utilisateur une fois
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            fullName = user.displayName ?: ""
            email = user.email ?: ""
        }
    }

    // Gérer les résultats de la mise à jour du profil
    LaunchedEffect(profileUpdateResult) {
        when (val result = profileUpdateResult) {
            is AuthResult.Success -> {
                if (result.user != null) { // Vérifier si c'est une vraie réussite de mise à jour
                    Toast.makeText(context, "Profil mis à jour avec succès !", Toast.LENGTH_SHORT).show()
                    currentPasswordForReauth = "" // Vider les champs de mot de passe
                    newPassword = ""
                    confirmNewPassword = ""
                    authViewModel.resetProfileUpdateResult() // Important pour ne pas redéclencher
                }
            }
            is AuthResult.Error -> {
                Toast.makeText(context, "Erreur: ${result.exception.localizedMessage}", Toast.LENGTH_LONG).show()
                authViewModel.resetProfileUpdateResult()
            }
            is AuthResult.Loading -> { /* Afficher un indicateur de chargement si besoin */ }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUser == null) {
                Text("Chargement du profil ou utilisateur non connecté...")
                return@Scaffold
            }

            Text(
                "Modifier mes informations",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nom complet") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Adresse e-mail") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Section pour changer le mot de passe (ou l'e-mail si besoin de ré-auth)
            Text(
                "Pour modifier l'e-mail ou le mot de passe, entrez votre mot de passe actuel :",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp, top = 16.dp)
            )
            OutlinedTextField(
                value = currentPasswordForReauth,
                onValueChange = { currentPasswordForReauth = it },
                label = { Text("Mot de passe actuel (pour validation)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nouveau mot de passe (laisser vide pour ne pas changer)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text("Confirmer nouveau mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = newPassword.isNotBlank() && newPassword != confirmNewPassword
            )
            if (newPassword.isNotBlank() && newPassword != confirmNewPassword) {
                Text("Les nouveaux mots de passe ne correspondent pas.", color = MaterialTheme.colorScheme.error)
            }


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Valider le mot de passe actuel si on change l'e-mail ou le mdp
                    val needsReauth = (email != currentUser?.email || newPassword.isNotBlank()) && currentPasswordForReauth.isBlank()
                    if (needsReauth) {
                        Toast.makeText(context, "Veuillez entrer votre mot de passe actuel pour valider les changements sensibles.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (fullName != currentUser?.displayName) {
                        authViewModel.updateUserDisplayName(fullName)
                    }
                    if (email != currentUser?.email && email.isNotBlank()) {
                        authViewModel.updateUserEmail(email, currentPasswordForReauth)
                    }
                    if (newPassword.isNotBlank()) {
                        if (newPassword == confirmNewPassword) {
                            authViewModel.updateUserPassword(newPassword, currentPasswordForReauth)
                        } else {
                            Toast.makeText(context, "Les nouveaux mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = profileUpdateResult !is AuthResult.Loading // Désactiver pendant le chargement
            ) {
                if (profileUpdateResult is AuthResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Enregistrer les modifications")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton( // Bouton de déconnexion avec un style différent
                onClick = {
                    authViewModel.logoutUser()
                    // Naviguer vers l'écran de connexion/principal après déconnexion
                    navController.navigate(AppRoutes.LOGIN_SCREEN) {
                        popUpTo(navController.graph.id) { inclusive = true } // Nettoie toute la pile
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CustomRed)
            ) {
                Text("Déconnexion", color = CustomRed)
            }
        }
    }
}

// Votre Preview original, ajusté pour utiliser AuthViewModel
// @Preview(showBackground = true)
// @Composable
// fun ProfileScreenPreview() {
//     // Pour le preview, il est difficile de simuler AuthViewModel sans plus de setup.
//     // Vous pouvez créer un NavController factice.
//     // LesPetitesPucesTheme { // Si vous utilisez un thème
//         ProfileScreen(navController = rememberNavController(), authViewModel = viewModel()) // viewModel() donnera une instance factice si pas de provider
//     // }
// }