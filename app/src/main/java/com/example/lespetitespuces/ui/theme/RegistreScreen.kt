package com.example.lespetitespuces.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed
import com.example.lespetitespuces.ui.theme.ItalianoFontFamily
import com.example.lespetitespuces.viewmodel.AuthResult
import com.example.lespetitespuces.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var formToShow by remember { mutableStateOf("signup") }

    val authResultState by authViewModel.authResult.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authResultState) {
        if (authResultState is AuthResult.Success && (authResultState as AuthResult.Success).user != null) {
            Toast.makeText(context, "Inscription réussie!", Toast.LENGTH_SHORT).show()
            navController.navigate(AppRoutes.MAIN_SCREEN) {
                popUpTo(AppRoutes.REGISTER_SCREEN) { inclusive = true }
                popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
            }
        } else if (authResultState is AuthResult.Error) {
            Toast.makeText(context, "Erreur: ${(authResultState as AuthResult.Error).exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
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

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(color = CustomRed)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomRed)
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        navController.navigate(AppRoutes.LOGIN_SCREEN) {
                            popUpTo(AppRoutes.REGISTER_SCREEN) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .clip(RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)),
                    shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                ) {
                    Text("Login")
                }

                Button(
                    onClick = { formToShow = "signup" },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .clip(RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp)),
                    shape = RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp, topStart = 0.dp, bottomStart = 0.dp)
                ) {
                    Text("Signup")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomTextField(label = "Nom complet", value = fullName, onValueChange = { fullName = it })
                CustomTextField(label = "Email", value = email, onValueChange = { email = it })
                CustomTextField(label = "Mot de passe", value = password, onValueChange = { password = it }, isPassword = true)

                Spacer(modifier = Modifier.height(12.dp))

                if (authResultState is AuthResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White
                    )
                } else {
                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.registerUser(email, password)
                            } else {
                                Toast.makeText(context, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Register", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column {
        Text(
            label,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
