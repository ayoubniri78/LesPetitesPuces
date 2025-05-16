// Fichier : com/example/lespetitespuces/ui/screens/ValidatePaymentScreen.kt
// (ou com/example/lespetitespuces/ui/theme/ValidatePaymentScreen.kt si vous l'avez mis là)
package com.example.lespetitespuces.ui.screens // Ou com.example.lespetitespuces.ui.theme selon où vous l'avez mis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
// Utiliser les composants Material 3 si le reste de l'app les utilise
import androidx.compose.material3.Button // de material3
import androidx.compose.material3.ButtonDefaults // de material3
import androidx.compose.material3.Icon // de material3
import androidx.compose.material3.Text // de material3
import androidx.compose.material3.MaterialTheme // Pour un accès potentiel au thème
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Pour le Preview
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed // Assurez-vous que CustomRed est défini
import com.example.lespetitespuces.ui.theme.ItalianoFontFamily // Assurez-vous que ItalianoFontFamily est défini
import com.example.lespetitespuces.ui.theme.LightGrey // Assurez-vous que LightGrey est défini
import com.example.lespetitespuces.ui.theme.White // Assurez-vous que White est défini (ou utilisez Color.White)


// Définitions placeholder si elles ne sont pas dans votre Theme.kt
// val CustomRed = Color(0xFFD32F2F) // Exemple de rouge
// val LightGrey = Color(0xFFF5F5F5) // Exemple de gris clair
// val White = Color.White
// val ItalianoFontFamily = FontFamily.Serif // Exemple de police

@Composable
fun ValidatePaymentScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fond général de l'écran
            .padding(top = 16.dp) // Un peu d'espace en haut
    ) {
        // Header Section
        Text(
            text = "Les petites puces",
            color = MaterialTheme.colorScheme.onBackground, // Couleur de texte du thème
            fontSize = 50.sp,
            fontFamily = ItalianoFontFamily,
            modifier = Modifier.padding(top = 60.dp)
        )

        Text(
            text = "L'art de game avec style",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 40.sp,
            fontFamily = ItalianoFontFamily, // Assurez-vous que la variable de police est correcte
            modifier = Modifier.padding(bottom = 20.dp) // Espace avant la carte
        )

        // Carte de Succès (selon votre design)
        Column(
            modifier = Modifier
                .fillMaxWidth() // Prendra la largeur disponible
                .padding(horizontal = 30.dp) // Marge horizontale pour la carte
                .weight(1f) // Pour centrer verticalement la carte si l'espace le permet
                .clip(RoundedCornerShape(40.dp)) // Coins plus arrondis que 70dp pour un look plus doux
                .background(color = LightGrey) // Votre LightGrey
                .padding(top = 32.dp, bottom = 32.dp, start = 24.dp, end = 24.dp), // Padding intérieur
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centrer le contenu verticalement dans la carte
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Success",
                tint = CustomRed, // Votre CustomRed
                modifier = Modifier.size(78.dp)
            )

            Spacer(modifier = Modifier.height(8.dp)) // Espace réduit

            Text(
                text = "Succès !",
                fontSize = 45.sp, // Légèrement réduit pour un meilleur équilibre
                color = CustomRed,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
                // modifier = Modifier.offset(y = (-30).dp) // L'offset n'est plus nécessaire avec Arrangement.Center
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espace ajusté

            Text(
                text = buildAnnotatedString {
                    append("Nous vous\n")
                    append("remercions pour votre\n")
                    append("achat")
                },
                fontSize = 24.sp, // Légèrement réduit
                fontWeight = FontWeight.Bold, // Gardé en gras selon votre design
                textAlign = TextAlign.Center,
                lineHeight = 30.sp // Espacement de ligne ajusté
                // modifier = Modifier.offset(y = (-30).dp) // Plus nécessaire
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espace avant le bouton

            Button(
                onClick = {
                    navController.navigate(AppRoutes.MAIN_SCREEN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomRed,
                    contentColor = White // Votre White ou Color.White
                ),
                modifier = Modifier
                    .width(180.dp) // Légèrement plus large
                    .height(50.dp), // Hauteur standard
                shape = RoundedCornerShape(15.dp) // Coins arrondis standards pour un bouton
                // .offset(y = (0).dp) // L'offset n'est généralement pas nécessaire avec une bonne structure de layout
            ) {
                Text("Retour à l'accueil", fontSize = 16.sp) // Texte plus descriptif
            }
        }
        Spacer(modifier = Modifier.height(30.dp)) // Espace en bas de l'écran
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121) // Fond sombre pour le preview
@Composable
fun ValidatePaymentScreenPreview() {
    // Pour le preview, vous pouvez envelopper avec votre thème si besoin
    // LesPetitesPucesTheme {
    ValidatePaymentScreen(navController = rememberNavController())
    // }
}