// Fichier : ui/screens/MainScreen.kt
package com.example.lespetitespuces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lespetitespuces.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.lespetitespuces.ui.theme.ItalianoFontFamily

val Italianno = FontFamily(
    Font(R.font.italianno_regular) // Assurez-vous que le fichier italianno_regular.ttf est dans res/font/
)

@Composable
fun MainScreen() {
    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter() },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                // Zone de contenu vide à remplir plus tard
                Text(
                    text = "Contenu principal à implémenter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Les petites puces",
                fontSize = 40.sp,
                fontFamily = Italianno,
            )
            Text(
                text = "L'art de gamer avec style",
                fontSize = 30.sp,
                fontFamily = Italianno,

            )
        }

        // Bouton "AN" rouge à droite
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Red, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AN",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AppFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Red)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterImageItem(
                drawableResId = R.drawable.home,
                contentDescription = "Accueil",
                onClick = { /* TODO */ }
            )
            FooterImageItem(
                drawableResId = R.drawable.favorite,
                contentDescription = "Favoris",
                onClick = { /* TODO */ }
            )
            FooterImageItem(
                drawableResId = R.drawable.support_agent,
                contentDescription = "support",
                onClick = { /* TODO */ }
            )
            FooterImageItem(
                drawableResId = R.drawable.account_circle,
                contentDescription = "account",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun FooterImageItem(
    drawableResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}