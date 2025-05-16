package com.example.lespetitespuces.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite // Pour le like, vous pouvez le remplacer par FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lespetitespuces.R // Assurez-vous d'avoir R.drawable.placeholder_image
import com.example.lespetitespuces.model.ItemsModel
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed // Assurez-vous que CustomRed est défini
// import com.example.lespetitespuces.ui.theme.ItalianoFontFamily // Si vous l'utilisez ici
import com.example.lespetitespuces.viewmodel.AuthViewModel
import com.example.lespetitespuces.viewmodel.CartActionResult
import com.example.lespetitespuces.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // Passé depuis AppNavigationGraph
    cartViewModel: CartViewModel = viewModel(), // Obtenu ici
    item: ItemsModel // Passé depuis AppNavigationGraph
) {
    var quantity by remember { mutableStateOf(1) }
    // Exemple de RAM, idéalement cela viendrait de item.availableRamOptions ou similaire
    val defaultRam = "8 GO" // Ou le premier de la liste des options de l'item
    var selectedRam by remember { mutableStateOf(defaultRam) }
    val ramOptions = remember(item) { /* item.ramOptions ?: */ listOf("32 GO", "16 GO", "8 GO") }
    val context = LocalContext.current

    val cartActionResult by cartViewModel.cartActionResult.observeAsState()

    LaunchedEffect(cartActionResult) {
        when (val result = cartActionResult) {
            is CartActionResult.Success -> {
                Toast.makeText(context, "Action panier réussie !", Toast.LENGTH_SHORT).show()
                cartViewModel.resetCartActionResult()
            }
            is CartActionResult.Error -> {
                Toast.makeText(context, "Erreur: ${result.message}", Toast.LENGTH_LONG).show()
                cartViewModel.resetCartActionResult()
            }
            else -> { /* Ne rien faire pour Loading ou Idle */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        item.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5)) // Couleur de fond comme dans votre design
                .padding(16.dp)
        ) {
            // Carte principale pour l'image et les détails initiaux
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.picUrl.firstOrNull()) // Afficher la première image
                            .crossfade(true)
//                            .placeholder(R.drawable.placeholder_image) // Assurez-vous que ce drawable existe
//                            .error(R.drawable.placeholder_image)
                            .build(),
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Fit // Ou Crop, selon le rendu souhaité
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
                            Text(" 94%", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp)) // Placeholder
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray // Ou MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp)) // Espace avant les options

            // Section Mémoire RAM
            Text(
                "Mémoire Ram :",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ramOptions.forEach { ram ->
                    OutlinedButton(
                        onClick = { selectedRam = ram },
                        shape = RoundedCornerShape(50), // Boutons ovales
                        border = BorderStroke(1.dp, if (selectedRam == ram) CustomRed else Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedRam == ram) CustomRed.copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = if (selectedRam == ram) CustomRed else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(ram)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Quantité et Prix
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                // horizontalArrangement = Arrangement.SpaceBetween // Sera géré par Spacer.weight(1f)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Quantité", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmallIconButton(icon = Icons.Filled.Remove, baseColor = CustomRed) { if (quantity > 1) quantity-- }
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        SmallIconButton(icon = Icons.Filled.Add, baseColor = CustomRed) {
                            // Idéalement, vérifier le stock ici aussi si l'achat est direct
                            if (quantity < item.stock) quantity++
                            else Toast.makeText(context, "Stock maximum atteint pour cet article.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Spacer(Modifier.weight(1f)) // Pour pousser le prix à droite
                Text(
                    text = "${String.format("%.0f", item.price * quantity)}dh", // Format sans décimales
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = CustomRed),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Boutons d'action
            Button( // Ajouter au panier
                onClick = {
                    if (authViewModel.isUserLoggedIn()) {
                        cartViewModel.addItemToCart(item, quantity)
                    } else {
                        Toast.makeText(context, "Veuillez vous connecter pour ajouter au panier", Toast.LENGTH_SHORT).show()
                        navController.navigate(AppRoutes.LOGIN_SCREEN)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray) // Gris foncé
            ) {
                Text("Ajouter au panier", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button( // Acheter maintenant
                onClick = {
                    if (authViewModel.isUserLoggedIn()) {
                        // Option 1: Ajouter au panier puis naviguer vers le panier
                        cartViewModel.addItemToCart(item, quantity) // Ajoute ou met à jour la quantité
                        // Attendre un court instant que l'ajout se fasse avant de naviguer
                        // ou mieux, observer le résultat de addItemToCart et naviguer au succès.
                        // Pour simplifier ici, on navigue directement :
                        navController.navigate(AppRoutes.CART_SCREEN)

                        // Option 2: Logique d'achat direct (plus complexe à gérer pour le stock)
                        // Toast.makeText(context, "Logique d'achat direct à implémenter", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(AppRoutes.LOGIN_SCREEN)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomRed) // Votre CustomRed
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
            Spacer(modifier = Modifier.height(16.dp)) // Espace en bas pour le défilement
        }
    }
}

@Composable
private fun SmallIconButton( // Renommé baseColor pour éviter la confusion avec le paramètre color de Icon
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    baseColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(baseColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}