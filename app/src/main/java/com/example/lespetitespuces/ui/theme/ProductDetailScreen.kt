package com.example.lespetitespuces.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Pour la flèche retour
import androidx.compose.material.icons.filled.Favorite // Pour le like
import androidx.compose.material.icons.filled.Remove // Pour le moins
import androidx.compose.material.icons.filled.Add // Pour le plus
import androidx.compose.material.icons.filled.Star // Pour le rating
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lespetitespuces.R // Assurez-vous que R est importé correctement
import com.example.lespetitespuces.model.ItemsModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    item: ItemsModel // L'item complet est passé ici
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedRam by remember { mutableStateOf("8 GO") } // Option RAM par défaut
    // Exemple de liste d'options RAM, vous devriez l'obtenir de votre ItemsModel si possible
    val ramOptions = listOf("32 GO", "16 GO", "8 GO")


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.title, maxLines = 1) }, // Le titre de l'item dans la barre
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Action de retour
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Ou une autre couleur
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Pour rendre la page défilable si le contenu est long
                .background(Color(0xFFF5F5F5)) // Couleur de fond légèrement grise comme dans le design
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.picUrl.firstOrNull()) // Afficher la première image
                            .crossfade(true)
//                            .placeholder(R.drawable.placeholder_image)
//                            .error(R.drawable.placeholder_image)
                            .build(),
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp) // Hauteur de l'image principale
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Fit // Ou Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                            Text(" ${item.rating}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(Icons.Filled.Favorite, contentDescription = "Likes", tint = Color.Red, modifier = Modifier.size(20.dp))
                            Text(" 94%", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp)) // Placeholder pour le % like
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Section Mémoire RAM
            Text("Mémoire Ram :", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ramOptions.forEach { ram ->
                    OutlinedButton(
                        onClick = { selectedRam = ram },
                        shape = RoundedCornerShape(50), // Boutons ovales
                        border = BorderStroke(1.dp, if (selectedRam == ram) Color.Red else Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedRam == ram) Color.Red.copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = if (selectedRam == ram) Color.Red else Color.DarkGray
                        )
                    ) {
                        Text(ram)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Quantité et Prix/Achat
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Quantité
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Quantité", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmallIconButton(icon = Icons.Filled.Remove, color = Color.Red) { if (quantity > 1) quantity-- }
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        SmallIconButton(icon = Icons.Filled.Add, color = Color.Red) { quantity++ }
                    }
                }
                Spacer(Modifier.weight(1f)) // Pour pousser le prix à droite
                // Prix
                Text(
                    // Formatter le prix si nécessaire
                    text = "${String.format("%.2f", item.price * quantity)}dh", // Prix total basé sur la quantité
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.Red),
                    textAlign = TextAlign.End
                )

            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: Logique d'achat */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)) // Marron foncé/rouge
            ) {
                Text("Acheter maintenant", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Livraison en 48 h partout au Maroc",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp)) // Espace en bas
        }
    }
}

@Composable
private fun SmallIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}