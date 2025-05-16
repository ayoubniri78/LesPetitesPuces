// fichier: com/example/lespetitespuces/ui/screens/LoginScreen.kt
package com.example.lespetitespuces.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog // Utiliser androidx.compose.material3.AlertDialog si possible
import androidx.compose.material.Button // Utiliser androidx.compose.material3.Button si possible
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text // Utiliser androidx.compose.material3.Text si possible
import androidx.compose.material.TextButton // Utiliser androidx.compose.material3.TextButton si possible
import androidx.compose.material.TextField // Utiliser androidx.compose.material3.TextField si possible
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed // Assurez-vous que ces couleurs sont définies
import com.example.lespetitespuces.ui.theme.ItalianoFontFamily // Assurez-vous que cette police est définie
import com.example.lespetitespuces.viewmodel.AuthResult
import com.example.lespetitespuces.viewmodel.AuthViewModel
import com.example.lespetitespuces.viewmodel.PasswordResetResult // Importer le nouveau sealed class

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    val authResultState by authViewModel.authResult.observeAsState()
    val passwordResetResultState by authViewModel.passwordResetResult.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authResultState) {
        // ... (logique existante pour la connexion)
        if (authResultState is AuthResult.Success && (authResultState as AuthResult.Success).user != null) {
            Toast.makeText(context, "Connexion réussie!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (authResultState is AuthResult.Error) {
            Toast.makeText(context, "Erreur de connexion: ${(authResultState as AuthResult.Error).exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    // Observer le résultat de la réinitialisation du mot de passe
    LaunchedEffect(passwordResetResultState) {
        when (val result = passwordResetResultState) {
            is PasswordResetResult.Success -> {
                Toast.makeText(context, "E-mail de réinitialisation envoyé à $resetEmail", Toast.LENGTH_LONG).show()
                showPasswordResetDialog = false // Fermer la dialogue
                resetEmail = "" // Réinitialiser l'e-mail de la dialogue
                authViewModel.resetPasswordResetState() // Remettre à Idle
            }
            is PasswordResetResult.Error -> {
                Toast.makeText(context, "Erreur: ${result.exception.localizedMessage}", Toast.LENGTH_LONG).show()
                // Ne pas fermer la dialogue automatiquement, laisser l'utilisateur corriger/réessayer
                authViewModel.resetPasswordResetState() // Remettre à Idle pour permettre une nouvelle tentative
            }
            is PasswordResetResult.Loading -> {
                // La dialogue peut afficher un indicateur de chargement
            }
            is PasswordResetResult.Idle -> {
                // Ne rien faire
            }
            null -> { /* Ne rien faire */ }
        }
    }


    if (showPasswordResetDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordResetDialog = false
                resetEmail = "" // Réinitialiser au cas où
                authViewModel.resetPasswordResetState() // Remettre à Idle si on annule
            },
            title = { Text("Réinitialiser le mot de passe") },
            text = {
                Column {
                    Text("Entrez votre adresse e-mail pour recevoir un lien de réinitialisation.")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Adresse e-mail") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (resetEmail.isNotBlank()) {
                            authViewModel.sendPasswordResetEmail(resetEmail)
                        } else {
                            Toast.makeText(context, "Veuillez entrer une adresse e-mail", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    if (passwordResetResultState is PasswordResetResult.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Envoyer")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordResetDialog = false
                    resetEmail = ""
                    authViewModel.resetPasswordResetState()
                }) {
                    Text("Annuler")
                }
            }
        )
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)
    ) {
        // ... (Header Section reste la même) ...
        Text(
            text = "Les petites puces",
            color = Color.Black,
            fontSize = 65.sp,
            fontFamily = ItalianoFontFamily,
            modifier = Modifier.padding(top = 60.dp)
        )
        Text(
            text = "L'art de game avec style",
            color = Color.Black,
            fontSize = 50.sp,
            fontFamily = ItalianoFontFamily,
        )


        // Form Section
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(color = CustomRed)
        ) {
            // ... (Toggle Buttons restent les mêmes, mais la logique onClick de "Signup" navigue) ...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomRed)
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button( // Bouton Login
                    onClick = { /* reste login */ },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).height(45.dp).clip(RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)),
                    shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                ) { Text("Login") }

                Button( // Bouton Signup (navigue vers RegisterScreen)
                    onClick = { navController.navigate(AppRoutes.REGISTER_SCREEN) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f).height(45.dp).clip(RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp)),
                    shape = RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp, topStart = 0.dp, bottomStart = 0.dp)
                ) { Text("Signup") }
            }


            // ... (Form Fields restent les mêmes) ...
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column { // Email
                    Text("Email", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.textFieldColors(

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                Column { // Password
                    Text("Password", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.textFieldColors(

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (authResultState is AuthResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.White)
                } else {
                    Button( // Login Button
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.loginUser(email, password)
                            } else {
                                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black, contentColor = Color.White)
                    ) { Text(text = "Login", fontSize = 20.sp) }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Mot de passe oublié ?",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { showPasswordResetDialog = true } // Affiche la dialogue
            )
        }
    }
}