package com.example.lespetitespuces

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroScreen()
        }
    }
}

val italianno = FontFamily(Font(R.font.italianno_regular))

@Composable
fun IntroScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Les petites puces.",
            style = TextStyle(
                fontFamily = italianno,
                fontSize = 48.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )
    }

    // Ajoutez ce bloc pour la navigation automatique
    LaunchedEffect(Unit) {
        delay(2000) // Délai de 2 secondes
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as? BaseActivity)?.finish() // Ferme l'IntroActivity
    }
}